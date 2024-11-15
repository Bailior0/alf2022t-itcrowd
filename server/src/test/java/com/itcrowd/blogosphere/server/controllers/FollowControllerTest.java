package com.itcrowd.blogosphere.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcrowd.blogosphere.server.model.Follow;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.FollowRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FollowControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    FollowRepository followRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService service;

    MockMvc mockMvc;

    User newUser1;

    User newUser2;

    @BeforeEach
    public void setTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        newUser1 = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .build();

        newUser2 = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser2")
                .password(passwordEncoder.encode("Pass"))
                .build();

        Mockito.when(userRepository.findById(newUser1.getId())).thenReturn(java.util.Optional.of(newUser1));
        Mockito.when(userRepository.findById(newUser2.getId())).thenReturn(java.util.Optional.of(newUser2));
        Mockito.when(service.getCurrentUser()).thenReturn(newUser1);
    }

    @Test
    public void isFollowTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/follow/" + newUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    public void isFollowTest_NoTarget() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/follow/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(""));
    }

    @Test
    public void followTest() throws Exception {
        var follow = Follow.builder().user(newUser1)
                .target(newUser2)
                .build();

        Mockito.when(followRepository.findByUserAndTarget(newUser1, newUser2)).thenReturn(java.util.Optional.of(follow));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/follow/" + newUser2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(""));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/follow/" + newUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void followTest_NoTarget() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/follow/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("TestUser"));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("No user with ID"));
    }

    @Test
    public void unfollowTest() throws Exception {
        Follow follow = new Follow();
        follow.setId(0L);
        follow.setTarget(newUser2);
        follow.setUser(newUser1);

        Mockito.when(followRepository.findByUserAndTarget(newUser1, newUser2)).thenReturn(java.util.Optional.of(follow));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/follow/" + newUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(""));
    }

    @Test
    public void unfollowTest_NoTarget() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/follow/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(""));
    }
}
