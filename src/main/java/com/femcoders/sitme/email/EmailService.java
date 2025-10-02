package com.femcoders.sitme.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendRegistrationEmail(String recipientEmail, String username) {

        sendEmail(recipientEmail, username, null, null, null,
                "registry-notification",
                "Successful registration | SitMe App");
    }

    public void sendReservationConfirmationEmail(String recipientEmail, String username, String spaceName, LocalDate reservationDate, String timeSlot) {

        sendEmail(recipientEmail, username, spaceName, reservationDate, timeSlot,
                "reservation-confirmation",
                "Reservation confirmed | SitMe App");
    }

    public void sendReservationUpdateEmail(String recipientEmail, String username, String spaceName, LocalDate reservationDate, String timeSlot) {

        sendEmail(recipientEmail, username, spaceName, reservationDate, timeSlot,
                "update-notification",
                "Reservation updated | SitMe App");
    }

    public void sendReservationCancellationEmail(String recipientEmail, String username, String spaceName, LocalDate reservationDate, String timeSlot) {

        sendEmail(recipientEmail, username, spaceName, reservationDate, timeSlot,
                "cancellation-notification",
                "Reservation cancelled | SitMe App");

    }

    private void sendEmail(String recipientEmail, String username, String spaceName, LocalDate reservationDate, String timeSlot, String templateName, String subject) {

        try {
            MimeMessage emailMessage = mailSender.createMimeMessage();

            MimeMessageHelper emailHelper = new MimeMessageHelper(emailMessage, true);

            emailHelper.setFrom("noreply@sitmeapp.com");
            emailHelper.setTo(recipientEmail);
            emailHelper.setSubject(subject);

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("frontendUrl", frontendUrl);
            if (spaceName != null) context.setVariable("spaceName", spaceName);
            if (reservationDate != null) context.setVariable("date", reservationDate.format(DATE_FORMATTER));
            if (timeSlot != null) context.setVariable("timeSlot", timeSlot);

            String emailHtmlContent = templateEngine.process(templateName, context);

            emailHelper.setText(emailHtmlContent, true);

            mailSender.send(emailMessage);

        } catch (Exception exception) {
            throw new RuntimeException("Failed to send email to " + recipientEmail, exception);
        }
    }
}
