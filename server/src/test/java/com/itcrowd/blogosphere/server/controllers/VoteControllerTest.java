package com.itcrowd.blogosphere.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.PostVote;
import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.NewVoteDTO;
import com.itcrowd.blogosphere.server.repository.PostRepository;
import com.itcrowd.blogosphere.server.repository.PostVoteRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoteControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostRepository postRepository;

    @MockBean
    PostVoteRepository voteRepository;

    MockMvc mockMvc;

    User user;

    Post post;

    NewVoteDTO voteDto;

    @BeforeEach
    public void setTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Role admin_role = new Role("ADMIN");

        user = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .roles(List.of(admin_role))
                .build();

        post = Post.builder()
                .id(UUID.randomUUID())
                .title("Post")
                .createDate(LocalDateTime.now())
                .content("Ok1")
                .author(user)
                .build();

        voteDto = new NewVoteDTO();
        voteDto.setAmount(1);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        Mockito.when(postRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(userRepository.save(any())).thenReturn(user);
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(java.util.Optional.of(user));
    }

    @Test
    public void voteTest() throws Exception {
        Authentication authentication  = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("TestUser", "Pass"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        PostVote vote = new PostVote();
        vote.setAmount(1);
        vote.setPost(post);
        vote.setUser(user);

        Mockito.when(voteRepository.save(vote)).thenReturn(vote);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/vote/" + post.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(voteDto))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.post").value(post.getId().toString()))
                .andExpect(jsonPath("$.data.user").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.amount").value(1));
    }

    @Test
    public void voteTest_NoPost() throws Exception {
        post.setArchive(true);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/vote/" + post.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(voteDto))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("The post has been archived, so it cannot be voted on."));
    }

    @Test
    public void voteTest_ArchivedPost() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/vote/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(voteDto))
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("The post doesn't exist"));
    }
}
