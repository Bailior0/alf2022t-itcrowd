package com.itcrowd.blogosphere.server.services;

import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.RoleRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void loadUserByUsernameTest() {
        var admin_role =new Role("ADMIN");
        roleRepository.save(admin_role);

        User newUser = User.builder()
                .email("mail")
                .username("TestUser")
                .password(passwordEncoder.encode("Pass"))
                .roles(List.of(admin_role))
                .build();

        userRepository.save(newUser);

        UserDetails actualUserDetails = customUserDetailsService.loadUserByUsername("TestUser");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                newUser.getUsername(),
                newUser.getPassword(),
                List.of( new SimpleGrantedAuthority("ADMIN"))
        );

        assertThat(actualUserDetails).isEqualTo(userDetails);
    }

    @Test
    public void loadUserByUsernameTest_NoUser() {
        String actualMessage = null;
        try {
            customUserDetailsService.loadUserByUsername("TestUser0");
        } catch (Exception e) {
            actualMessage = e.getMessage();
        }

        assertThat(actualMessage).isEqualTo("User not found with username or email: TestUser0");
    }
}
