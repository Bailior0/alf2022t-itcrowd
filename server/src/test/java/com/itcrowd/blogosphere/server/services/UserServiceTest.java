package com.itcrowd.blogosphere.server.services;

import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.RoleRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    @Transactional
    public void getCurrentUserTest() {
        var admin_role = roleRepository.save(new Role("ADMIN"));

        User newUser = User.builder()
                .email("mail")
                .username("NewTestUser")
                .password(passwordEncoder.encode("Pass"))
                .roles(List.of(admin_role))
                .build();

        userRepository.save(newUser);

        Authentication authentication  = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("NewTestUser", "Pass"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getCurrentUser();

        assertThat(user.getUsername()).isEqualTo(newUser.getUsername());
    }

    @Test
    public void getCurrentUserTest_NoUser() {
        String actualMessage = null;
        try {
            userService.getCurrentUser();
        } catch (Exception e) {
            actualMessage = e.getMessage();
        }

        assertThat(actualMessage).isEqualTo(null);
    }
}
