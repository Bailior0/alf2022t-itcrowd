package com.itcrowd.blogosphere.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail,
                                String body,
                                String subject) {
        Logger logger = Logger.getLogger("forumapp.EmailSenderService.sendSimpleEmail");
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("alf2022t.itcrowd@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);

            mailSender.send(message);
            logger.log(Level.INFO, "Email sent to "+ toEmail);
        }
        catch (Exception ignored) {
            logger.log(Level.SEVERE, "Could not send email.");
        }
    }
}
