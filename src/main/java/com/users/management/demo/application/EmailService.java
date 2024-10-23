package com.users.management.demo.application;

import com.users.management.demo.repositories.relational.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    //Async for avoiding broke Transaction
    @Async
    public void sendRegistrationEmail(UserEntity user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Our Service");
        message.setText("Dear " + user.getName() + ",\n\nThank you for registering with us!");
        mailSender.send(message);
    }
}
