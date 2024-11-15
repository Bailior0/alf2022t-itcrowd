package com.itcrowd.blogosphere.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.LoginDto;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    User newUser;

    MockMvc mockMvc;

    @BeforeEach
    public void setTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Role admin_role = new Role("ADMIN");

        newUser = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .roles(List.of(admin_role))
                .build();

        Mockito.when(userRepository.save(any())).thenReturn(newUser);
        Mockito.when(userRepository.findByUsername(newUser.getUsername())).thenReturn(java.util.Optional.of(newUser));
        Mockito.when(userRepository.findByUsernameOrEmail(newUser.getUsername(), newUser.getUsername())).thenReturn(java.util.Optional.of(newUser));
    }

    @Test
    public void registerUserTest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newUser));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(newUser.getUsername() + " registered"));
    }

    @Test
    public void registerUserTest_ExistingUsername() throws Exception {
        User newUser2 = User.builder()
                .id(UUID.randomUUID())
                .email("mail2")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .build();

        Mockito.when(userRepository.existsByUsername(newUser2.getUsername())).thenReturn(true);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newUser2));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Username is already taken!"));
    }

    @Test
    public void registerUserTest_ExistingEmail() throws Exception {
        User newUser2 = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser2")
                .password(passwordEncoder.encode("Pass"))
                .build();

        Mockito.when(userRepository.existsByEmail(newUser2.getEmail())).thenReturn(true);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newUser2));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Email is already taken!"));
    }

    @Test
    public void loginUserTest() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("TestUser");
        loginDto.setPassword("Pass");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loginDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("TestUser"));
    }

    @Test
    public void loginUserTest_IncorrectAuth() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("TestUser0");
        loginDto.setPassword("Pass");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loginDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Username or Password is incorrect"));
    }

    @Test
    public void getactiveUserTest() throws Exception {
        Mockito.when(service.getCurrentUser()).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/auth/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("TestUser"));
    }

    @Test
    public void getactiveUserTest_NotLogged() throws Exception {
        Mockito.when(service.getCurrentUser()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/auth/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("Not logged in"));
    }
}
