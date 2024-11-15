package com.itcrowd.blogosphere.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.NewPostDto;
import com.itcrowd.blogosphere.server.repository.PostRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@Validated
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerTest {
    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PostController postController;

    @MockBean
    UserService service;

    @MockBean
    PostRepository postRepository;

    @MockBean
    UserRepository userRepository;

    User user;
    Post post1;
    Post post2;
    Post post3;

    @BeforeEach
    public void setTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .build();

        post1 = Post.builder()
                .id(UUID.randomUUID())
                .title("Post1")
                .createDate(LocalDateTime.now())
                .content("Ok1")
                .author(user)
                .build();

        post2 = Post.builder()
                .id(UUID.randomUUID())
                .title("Post2")
                .createDate(LocalDateTime.now().minusWeeks(2).minusMinutes(1))
                .content("Ok2")
                .author(user)
                .archive(true)
                .build();

        post3 = Post.builder()
                .id(UUID.randomUUID())
                .title("Post3")
                .createDate(LocalDateTime.now().minusMinutes(10))
                .content("Ok3")
                .author(user)
                .build();

        ArrayList<Post> posts = new ArrayList<>(Arrays.asList(post1, post2, post3));

        Mockito.when(postRepository.findById(post1.getId())).thenReturn(java.util.Optional.of(post1));
        Mockito.when(postRepository.findById(post2.getId())).thenReturn(java.util.Optional.of(post2));
        Mockito.when(postRepository.findById(post3.getId())).thenReturn(java.util.Optional.of(post3));

        Mockito.when(postRepository.findAll()).thenReturn(posts);

        Mockito.when(service.getCurrentUser()).thenReturn(user);
    }

    @Test
    public void getPostByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/" + post1.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.title").value("Post1"))
                .andExpect(jsonPath("$.content").value("Ok1"))
                .andExpect(jsonPath("$.author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/" + post2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.title").value("Post2"))
                .andExpect(jsonPath("$.content").value("Ok2"))
                .andExpect(jsonPath("$.author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/" + post3.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.title").value("Post3"))
                .andExpect(jsonPath("$.content").value("Ok3"))
                .andExpect(jsonPath("$.author").value(user.getId().toString()));
    }

    @Test
    public void getTopPostTest() throws Exception {
        Page<Post> posts = new PageImpl<>(Arrays.asList(post3, post1, post2));

        Mockito.when(postRepository.findAllByParentIsNullOrderByCreateDateDesc(PageRequest.of(0,10, Sort.unsorted()))).thenReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[0].title").value("Post3"))
                .andExpect(jsonPath("$.data.[0].content").value("Ok3"))
                .andExpect(jsonPath("$.data.[0].author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[1].title").value("Post1"))
                .andExpect(jsonPath("$.data.[1].content").value("Ok1"))
                .andExpect(jsonPath("$.data.[1].author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[2].title").value("Post2"))
                .andExpect(jsonPath("$.data.[2].content").value("Ok2"))
                .andExpect(jsonPath("$.data.[2].author").value(user.getId().toString()));
    }

    @Test
    public void getUserFeedTest() throws Exception {
        Page<Post> posts = new PageImpl<>(Arrays.asList(post3, post1, post2));

        Mockito.when(postRepository.getUserFeed(user, PageRequest.of(0,10, Sort.unsorted()))).thenReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[0].title").value("Post3"))
                .andExpect(jsonPath("$.data.[0].content").value("Ok3"))
                .andExpect(jsonPath("$.data.[0].author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[1].title").value("Post1"))
                .andExpect(jsonPath("$.data.[1].content").value("Ok1"))
                .andExpect(jsonPath("$.data.[1].author").value(user.getId().toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.data.[2].title").value("Post2"))
                .andExpect(jsonPath("$.data.[2].content").value("Ok2"))
                .andExpect(jsonPath("$.data.[2].author").value(user.getId().toString()));
    }

    @Test
    public void makePostTest() throws Exception {
        Post newPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now())
                .content("NewOk")
                .author(user)
                .build();

        NewPostDto newPostDto = new NewPostDto();
        newPostDto.setAuthor(user);
        newPostDto.setTitle("NewPost");
        newPostDto.setContent("NewOk");

        Mockito.when(postRepository.save(any())).thenReturn(newPost);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newPostDto))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.title").value("NewPost"))
                .andExpect(jsonPath("$.data.content").value("NewOk"))
                .andExpect(jsonPath("$.data.author").value(user.getId().toString()));
    }

    @Test
    public void getSubPostsTest() throws Exception {
        Post subPost1 = Post.builder()
                .id(UUID.randomUUID())
                .title("SubPost1")
                .createDate(LocalDateTime.now())
                .content("SubOk")
                .author(user)
                .parent(post1)
                .build();

        Post subPost2 = Post.builder()
                .id(UUID.randomUUID())
                .title("SubPost2")
                .createDate(LocalDateTime.now())
                .content("SubOk2")
                .author(user)
                .parent(post1)
                .build();

        Page<Post> subPosts = new PageImpl<>(Arrays.asList(subPost1, subPost2));

        Mockito.when(postRepository.getPostsByParentOrderByCreateDateDesc(
                post1,
                PageRequest.of(0,10).withPage(0))
        ).thenReturn(subPosts);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/" + post1.getId().toString() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data.[0].title").value("SubPost1"))
                .andExpect(jsonPath("$.data.[0].content").value("SubOk"))
                .andExpect(jsonPath("$.data.[0].author").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.[1].title").value("SubPost2"))
                .andExpect(jsonPath("$.data.[1].content").value("SubOk2"))
                .andExpect(jsonPath("$.data.[1].author").value(user.getId().toString()));
    }

    @Test
    public void getSubPostsTest_NoPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/" + UUID.randomUUID() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("No post exists with this ID"));
    }

    @Test
    public void makeSubPostTest() throws Exception {
        Post newSubPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now())
                .content("NewOk")
                .author(user)
                .build();

        NewPostDto newPostDto = new NewPostDto();
        newPostDto.setAuthor(user);
        newPostDto.setTitle("NewPost");
        newPostDto.setContent("NewOk");

        Mockito.when(postRepository.save(any())).thenReturn(newSubPost);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/posts/" + post1.getId().toString() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newPostDto))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.title").value("NewPost"))
                .andExpect(jsonPath("$.data.content").value("NewOk"))
                .andExpect(jsonPath("$.data.author").value(user.getId().toString()));
    }

    @Test
    public void makeSubPostTest_NoPost() throws Exception {
        Post newSubPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now())
                .content("NewOk")
                .author(user)
                .build();

        Mockito.when(postRepository.save(any())).thenReturn(newSubPost);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/posts/" + UUID.randomUUID() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newSubPost))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("No post exists with this ID"));
    }

    @Test
    public void updatePostTest() throws Exception {
        post1.setTitle("EditedTitle");
        post1.setContent("EditedContent");

        Mockito.when(postRepository.save(any())).thenReturn(post1);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/posts/" + post1.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(post1))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.title").value("EditedTitle"))
                .andExpect(jsonPath("$.data.content").value("EditedContent"))
                .andExpect(jsonPath("$.data.author").value(user.getId().toString()));
    }

    @Test
    public void updatePostTest_NoPost() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/posts/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(post2))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("The post doesn't exist"));
    }

    @Test
    public void updatePostTest_Archived() throws Exception {
        post2.setTitle("EditedTitle");
        post2.setContent("EditedContent");

        Mockito.when(postRepository.save(any())).thenReturn(post2);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/posts/" + post2.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(post2))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("The post has been archived, so it cannot be edited."));
    }

    @Test
    public void updatePostTest_NotAuthorized() throws Exception {
        post1.setTitle("EditedTitle");
        post1.setContent("EditedContent");

        Mockito.when(postRepository.save(any())).thenReturn(post1);
        Mockito.when(service.getCurrentUser()).thenReturn(new User());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/posts/" + post1.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(post1))
                .with(user("Nigerian Prince"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Post has not been updated."));
    }

    @Test
    public void deletePostTest() throws Exception {
        Mockito.when(postRepository.findById(post3.getId())).thenReturn(java.util.Optional.of(post3));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/posts/" + post3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Post has been deleted."));
    }

    @Test
    public void deletePostTest_NoPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/posts/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("No such post exists."));
    }

    @Test
    public void deletePostTest_NotAuthorized() throws Exception {
        Mockito.when(postRepository.findById(post3.getId())).thenReturn(java.util.Optional.of(post3));
        Mockito.when(service.getCurrentUser()).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/posts/" + post3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("Nigerian Prince")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Post has not been deleted."));
    }

    @Test
    public void checkPostArchiveTest() {
        Post newPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now().minusWeeks(2).minusMinutes(1))
                .content("NewOk")
                .author(user)
                .build();

        Mockito.when(postRepository.findAll()).thenReturn(List.of(newPost));
        postController.checkArchive();

        assertThat(newPost.isArchive()).isEqualTo(true);
    }

    @Test
    public void checkPostArchiveTest_NotArchived() {
        Post newPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now().minusWeeks(2).plusMinutes(1))
                .content("NewOk")
                .author(user)
                .build();

        Mockito.when(postRepository.findAll()).thenReturn(List.of(newPost));
        postController.checkArchive();

        assertThat(newPost.isArchive()).isEqualTo(false);
    }

    @Test
    public void newYearPostTest() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        postController.newYearPost();

        Post newYearPost = Post.builder()
                .title("BUEK!")
                .content("Happy New Year!")
                .author(user)
                .build();

        Mockito.when(postRepository.save(any())).thenReturn(newYearPost);

        verify(postRepository, atLeast(1)).save(newYearPost);
    }

    @Test
    public void deletePostAfterYearsTest() {
        Post newPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now().minusYears(10).minusMinutes(1))
                .content("NewOk")
                .author(user)
                .build();

        Mockito.when(postRepository.findAll()).thenReturn(List.of(newPost));
        postController.deletePostAfterYears();

        verify(postRepository, times(1)).delete(newPost);
    }

    @Test
    public void deletePostAfterYearsTest_NotDeleted() {
        Post newPost = Post.builder()
                .id(UUID.randomUUID())
                .title("NewPost")
                .createDate(LocalDateTime.now().minusYears(10).plusMinutes(1))
                .content("NewOk")
                .author(user)
                .build();

        Mockito.when(postRepository.findAll()).thenReturn(List.of(newPost));
        postController.deletePostAfterYears();

        verify(postRepository, never()).delete(newPost);
    }
}