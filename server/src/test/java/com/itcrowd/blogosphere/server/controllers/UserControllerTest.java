package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.EmailSenderService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserController userController;

    @MockBean
    private EmailSenderService senderService;

    MockMvc mockMvc;

    User user;

    @BeforeEach
    public void setTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Role admin_role = new Role();
        admin_role.setName("ADMIN");

        user = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .roles(List.of(admin_role))
                .registerDate(LocalDateTime.now().minusWeeks(1).minusHours(1))
                .build();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/" + user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.email").value("mail"))
                .andExpect(jsonPath("$.data.username").value("TestUser"));
    }

    @Test
    public void getUserByIdTest_NoUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TestUser")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").value("No such user"));
    }

    @Test
    public void userRegisteredWeekAgoEmailTest() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        userController.userRegisteredWeekAgoEmail();

        verify(senderService, times(1)).sendSimpleEmail(user.getEmail(), "A week has passed since you registered! \uD83C\uDF89 ", "Reminder");
    }

    @Test
    public void userRegisteredWeekAgoEmailTest_WrongDate() {
        User userLate = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .registerDate(LocalDateTime.now().minusWeeks(1).minusDays(1))
                .build();

        User userEarly = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .registerDate(LocalDateTime.now().minusWeeks(1).plusDays(1))
                .build();

        User userDateless = User.builder()
                .id(UUID.randomUUID())
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .build();

        Mockito.when(userRepository.findAll()).thenReturn(List.of(userLate, userEarly, userDateless));
        userController.userRegisteredWeekAgoEmail();

        verify(senderService, never()).sendSimpleEmail(user.getEmail(), "A week has passed since you registered! \uD83C\uDF89 ", "Nigerian prince");
    }
}
