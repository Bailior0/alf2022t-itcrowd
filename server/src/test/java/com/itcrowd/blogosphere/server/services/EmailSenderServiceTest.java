package com.itcrowd.blogosphere.server.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService senderService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void emailSentTest() {
        senderService.sendSimpleEmail("xxx@gmail.com", "A week has passed since you registered! \uD83C\uDF89 ", "Nigerian prince");

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("alf2022t.itcrowd@gmail.com");
        message.setTo("xxx@gmail.com");
        message.setText("A week has passed since you registered! \uD83C\uDF89 ");
        message.setSubject("Nigerian prince");

        verify(mailSender, times(1)).send(message);
    }
}
