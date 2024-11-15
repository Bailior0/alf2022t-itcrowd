package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.ApiResponseBuilder;
import com.itcrowd.blogosphere.server.payload.NewPostDto;
import com.itcrowd.blogosphere.server.payload.PostDto;
import com.itcrowd.blogosphere.server.repository.PostRepository;

import com.itcrowd.blogosphere.server.repository.PostVoteRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController()
@EnableScheduling
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostVoteRepository voteRepository;

    @Autowired
    UserService auth;

    @Autowired
    private ConversionService conversionService;

    @GetMapping("/{id}")
    public PostDto get(@PathVariable UUID id){
        var post = postRepository.findById(id);

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"/get post:"+id );
        return conversionService.convert(post.orElseGet(Post::new), PostDto.class);
    }

    @GetMapping("/top")
    public ResponseEntity<?> get(@RequestParam(required = false, defaultValue = "10") Optional<Integer> count){

        var _count = count.isPresent() ? count.get() : 10;

        var post = postRepository.findAllByParentIsNullOrderByCreateDateDesc(PageRequest.of(0,_count, Sort.unsorted()));

        var res = post.stream().map(p -> conversionService.convert(p, PostDto.class))
                .limit(_count)
                .toList();

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"/top " );
        return ApiResponseBuilder.ok(res);
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getUserFeed(@RequestParam(required = false, defaultValue = "10") Optional<Integer> count){

        var _count = count.isPresent() ? count.get() : 10;

        var post = postRepository.getUserFeed(auth.getCurrentUser(),PageRequest.of(0,_count, Sort.unsorted()));

        var res = post.stream().map(p -> conversionService.convert(p, PostDto.class))
                .limit(_count)
                .toList();

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"/feed for " + auth.getCurrentUser().getUsername() );
        return ApiResponseBuilder.ok(res);
    }


    @PostMapping("/")
    public ResponseEntity<?> make(@RequestBody NewPostDto newPost){
        var post = Post.fromNew(newPost);

        post.setAuthor(auth.getCurrentUser());

        var date = LocalDateTime.now();
        post.setCreateDate(date);

        post = postRepository.save(post);

        var res = conversionService.convert(post, PostDto.class);

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"/post post:"+post.getId());
        return ApiResponseBuilder.make(res, HttpStatus.OK);
    }

    @GetMapping("/{parent_id}/comments")
    public ResponseEntity<?> getComments(@PathVariable UUID parent_id,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false, defaultValue = "0") Integer page) {

        var parent = postRepository.findById(parent_id);

        if(parent.isEmpty()){
            return ApiResponseBuilder.error("No post exists with this ID");
        }

        var comments = postRepository.getPostsByParentOrderByCreateDateDesc(
                parent.get(),
                PageRequest.of(0,pageSize).withPage(page)
        );

        var res = comments.map(p -> conversionService.convert(p, PostDto.class)).toList();

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"get /"+parent_id+"/comments");
        return ApiResponseBuilder.ok(res);
    }

    @PostMapping("/{parent_id}/comments")
    public ResponseEntity<?> addSubPost(@PathVariable UUID parent_id,
                                        @Valid @RequestBody NewPostDto newPostDto) {
        var parent = postRepository.findById(parent_id);

        if(parent.isEmpty()){
            return ApiResponseBuilder.error("No post exists with this ID");
        }

        var newPost = Post.fromNew(newPostDto);

        newPost.setParent(parent.get());

        var date = LocalDateTime.now();
        newPost.setCreateDate(date);
        newPost.setAuthor(auth.getCurrentUser());

        var post = postRepository.save(newPost);

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"post /"+parent_id+"/comments");
        return ApiResponseBuilder.ok(conversionService.convert(post, PostDto.class));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@RequestBody NewPostDto newPost, @PathVariable UUID id) {
        var post = postRepository.findById(id);
        if(post.isEmpty()){
            return ApiResponseBuilder.error("The post doesn't exist");
        }
        if(post.get().isArchive()) {
            return ApiResponseBuilder.error("The post has been archived, so it cannot be edited.");
        }

        User u = auth.getCurrentUser();


        Post editedPost = post.get();
        Logger logger = Logger.getLogger("forumapp.PostController");

        if((u.getRoles() != null && u.getRoles().contains("ADMIN")) || editedPost.getAuthor() == u ) {
            editedPost.setTitle(newPost.getTitle());
            editedPost.setContent(newPost.getContent());

            var date = LocalDateTime.now();
            editedPost.setCreateDate(date);

            editedPost = postRepository.save(editedPost);

            var res = conversionService.convert(editedPost, PostDto.class);

            logger.log(Level.INFO,"put /" + editedPost.getId());
            return ApiResponseBuilder.make(res, HttpStatus.OK);
        }

        logger.log(Level.INFO,"Not authorized update /" + editedPost.getId());
        return  ApiResponseBuilder.error("Post has not been updated.");

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (postRepository.findById(id).isEmpty()) {
            return ApiResponseBuilder.error("No such post exists.");
        }

        User u = auth.getCurrentUser();
        Post p = postRepository.findById(id).get();
        Logger logger = Logger.getLogger("forumapp.PostController");

        if((u.getRoles() != null && u.getRoles().stream().anyMatch(r-> r.getName().equals("ADMIN"))) || p.getAuthor() == u ){
            postRepository.deleteById(id);

            logger.log(Level.INFO,"delete /"+id);
            return ApiResponseBuilder.ok("Post has been deleted.");
        }

        logger.log(Level.INFO,"Not authorized delete /"+id);
        return ApiResponseBuilder.error("Post has not been deleted.");
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkArchive() {
        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"scheduled archive posts started");
        int i = 0;
        var date = LocalDateTime.now();
        var posts = postRepository.findAll();
        for(Post post : posts) {
            if(post.getCreateDate().plusWeeks(2).isBefore(date)) {
                post.setArchive(true);
                i++;
                logger.log(Level.INFO,"archived post: "+post.getId());
            }
        }
        logger.log(Level.INFO,"scheduled archive posts finished, archived: "+ i+" posts");
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void newYearPost() {
        var users = userRepository.findAll();
        int i = 0;
        for(User user : users) {
            Post post = Post.builder()
                    .title("BUEK!")
                    .content("Happy New Year!")
                    .author(user)
                    .build();
            postRepository.save(post);
            i++;
        }

        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"Posted " + i + "new year post(s)");
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void deletePostAfterYears() {
        Logger logger = Logger.getLogger("forumapp.PostController");
        logger.log(Level.INFO,"deletePostAfterYears started");
        int i = 0;
        var date = LocalDateTime.now();
        var posts = postRepository.findAll();
        for(Post post : posts) {
            if(post.getCreateDate().plusYears(10).isBefore(date)) {
                postRepository.delete(post);
                logger.log(Level.INFO,"deletePostAfterYears started");
                i++;
            }
        }
        logger.log(Level.INFO,"deletePostAfterYears finished, deleted: "+ i+" posts");
    }
}
