package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.*;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.EmailSenderService;
import com.itcrowd.blogosphere.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private EmailSenderService senderService;

    Logger logger;
    public AuthController() {
        logger = Logger.getLogger("forumapp.AuthController");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto){

        Authentication authentication;

        try{
            authentication  = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if(authentication.isAuthenticated()){
                var user = userRepository.findByUsername(authentication.getName()).orElse(new User());
                logger.log(Level.INFO,"/signin successful");
                return ApiResponseBuilder.ok(conversionService.convert(user, UserDto.class));
            }
        }
        catch (Exception ignored) { }
        logger.log(Level.WARNING,"/signin unsuccessful");
        return ApiResponseBuilder.error("Username or Password is incorrect");
    }

    @GetMapping("/")
    public ResponseEntity<?> getactiveUser(){

        if(userService.getCurrentUser() == null){
            return ApiResponseBuilder.error("Not logged in");
        }

        var res = conversionService.convert(userService.getCurrentUser(), UserDto.class);

        return ApiResponseBuilder.ok(res);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto){

        Logger logger = Logger.getLogger("forumapp.AuthController");
        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){

            logger.log(Level.WARNING,"/signup username taken");
            return ApiResponseBuilder.error("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){

            logger.log(Level.WARNING,"/signup email taken");
            return ApiResponseBuilder.error("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();

        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        var date = LocalDateTime.now();
        user.setRegisterDate(date);

        userRepository.save(user);

        senderService.sendSimpleEmail(user.getEmail(), "You have successfully registered! \uD83C\uDF89 ", "Registration");

        logger.log(Level.INFO,"/signup registered");
        return ApiResponseBuilder.ok(user.getUsername() + " registered");
    }
}