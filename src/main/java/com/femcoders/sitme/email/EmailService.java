package com.femcoders.sitme.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendRegistrationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@sitmeapp.com");
        message.setTo(toEmail);
        message.setSubject("Successful registration");
        message.setText("Hi, " + username + "!\n\nYour registration was successful.");

        mailSender.send(message);
    }
}
