package com.itcrowd.blogosphere.server.controllers;

import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.ApiResponseBuilder;
import com.itcrowd.blogosphere.server.payload.UserDto;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import com.itcrowd.blogosphere.server.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController()
@EnableScheduling
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private EmailSenderService senderService;

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable UUID id) {
        //TODO: Get from db
        var user = userRepository.findById(id);

        if(user.isPresent()){
            return ApiResponseBuilder.make(conversionService.convert(user.get(),UserDto.class), HttpStatus.OK);
        }

        return ApiResponseBuilder.error("No such user");
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void userRegisteredWeekAgoEmail() {
        var date = LocalDateTime.now();
        var users = userRepository.findAll();
        for(User user : users) {
            if(user.getRegisterDate() == null){
                return;
            }

            if(user.getRegisterDate().plusWeeks(1).isBefore(date) && user.getRegisterDate().plusWeeks(1).plusDays(1).isAfter(date)) {
                senderService.sendSimpleEmail(user.getEmail(), "A week has passed since you registered! \uD83C\uDF89 ", "Reminder");
            }
        }
    }
}
