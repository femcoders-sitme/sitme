package com.femcoders.sitme.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private  final JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Successful registration");
        message.setText("Hi, " + username + "!\n\nYour registration was successful.");

        javaMailSender.send(message);
    }
}
