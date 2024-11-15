package com.itcrowd.blogosphere.server.services;

import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Logger logger = Logger.getLogger("forumapp.CustomerUserDetailsService.loadUser");
        User user = userRepository
               .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
               .orElseThrow(() -> {
                   String errText = "User not found with username or email: " + usernameOrEmail+"";
                   logger.log(Level.SEVERE, errText);
                   return new UsernameNotFoundException(errText);
               });

        logger.log(Level.INFO, "User found: "+ usernameOrEmail);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}