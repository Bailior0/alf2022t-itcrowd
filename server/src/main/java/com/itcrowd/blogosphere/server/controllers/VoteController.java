package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.PostVote;
import com.itcrowd.blogosphere.server.payload.ApiResponseBuilder;
import com.itcrowd.blogosphere.server.payload.NewVoteDTO;
import com.itcrowd.blogosphere.server.payload.VoteDTO;
import com.itcrowd.blogosphere.server.repository.PostRepository;
import com.itcrowd.blogosphere.server.repository.PostVoteRepository;
import com.itcrowd.blogosphere.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/api/vote")
public class VoteController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostVoteRepository voteRepository;

    @Autowired
    UserService auth;

    @Autowired
    private ConversionService conversionService;

    @PostMapping("/{id}")
    public ResponseEntity<?> vote(@PathVariable UUID id,
                                  @RequestBody NewVoteDTO voteDTO){

        var post = postRepository.findById(id);

        if(post.isEmpty()){
            return ApiResponseBuilder.error("The post doesn't exist");
        }
        if(post.get().isArchive()){
            return ApiResponseBuilder.error("The post has been archived, so it cannot be voted on.");
        }

        var vote = new PostVote();
        vote.setAmount(voteDTO.amount);
        vote.setPost(post.get());
        vote.setUser(auth.getCurrentUser());

        vote = voteRepository.save(vote);

        var dto = conversionService.convert(vote, VoteDTO.class);

        return ApiResponseBuilder.ok(dto);
    }
}
