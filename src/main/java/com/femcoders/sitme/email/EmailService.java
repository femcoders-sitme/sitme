package com.femcoders.sitme.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendRegistrationEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@sitmeapp.com");
            helper.setTo(toEmail);
            helper.setSubject("Successful registration | from SitMe");
            try(var inputStream = Objects.requireNonNull(EmailService.class.getResourceAsStream("/templates/registry-notification.html"))){
                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                html = html.replace("{{username}}", username);
                helper.setText(html, true);
            }
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
