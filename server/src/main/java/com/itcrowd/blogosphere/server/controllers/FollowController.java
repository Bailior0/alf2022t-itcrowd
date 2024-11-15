package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.Follow;
import com.itcrowd.blogosphere.server.payload.ApiResponseBuilder;
import com.itcrowd.blogosphere.server.repository.FollowRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @GetMapping("/{targetID}")
    public ResponseEntity<?> isfollow(@PathVariable UUID targetID){

        var user = userService.getCurrentUser();

        var target = userRepository.findById(targetID);

        if(target.isEmpty())
            return ApiResponseBuilder.ok("");

        var f = followRepository.findByUserAndTarget(user, target.get());

        return ApiResponseBuilder.ok(f.isPresent());
    }


    @PostMapping("/{target}")
    public ResponseEntity<?> follow(@PathVariable UUID target){

        var targetUser = userRepository.findById(target);
        if(targetUser.isEmpty())
        {
            return ApiResponseBuilder.error("No user with ID");
        }

        var res = Follow.builder().user(userService.getCurrentUser())
                .target(targetUser.get())
                .build();

        followRepository.save(res);

        return ApiResponseBuilder.ok("");
    }

    @DeleteMapping("/{targetID}")
    public ResponseEntity<?> unfollow(@PathVariable UUID targetID){

        var user = userService.getCurrentUser();

        var target = userRepository.findById(targetID);

        if(target.isEmpty())
            return ApiResponseBuilder.ok("");

        var f = followRepository.findByUserAndTarget(user, target.get());
        if(f.isPresent())
            followRepository.delete(f.get());

        return ApiResponseBuilder.ok("");
    }
}
