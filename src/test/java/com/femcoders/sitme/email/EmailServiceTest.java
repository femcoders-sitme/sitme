package com.femcoders.sitme.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(mailSender, templateEngine);
        emailService.getClass()
                .getDeclaredFields();
        org.springframework.test.util.ReflectionTestUtils
                .setField(emailService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void shouldSendRegistrationEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("registry-notification"), any(Context.class)))
                .thenReturn("<html>Email content</html>");

        emailService.sendRegistrationEmail("test@example.com", "Ana");

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("registry-notification"), any(Context.class));
    }

    @Test
    void shouldSendReservationConfirmationEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("reservation-confirmation"), any(Context.class)))
                .thenReturn("<html>Email content</html>");

        emailService.sendReservationConfirmationEmail(
                "test@example.com",
                "Laura",
                "Room A",
                LocalDate.of(2025, 10, 15),
                "MORNING"
        );

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("reservation-confirmation"), any(Context.class));
    }

    @Test
    void shouldSendReservationUpdateEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("update-notification"), any(Context.class)))
                .thenReturn("<html>Email content</html>");

        emailService.sendReservationUpdateEmail(
                "test@example.com",
                "Carmen",
                "Room B",
                LocalDate.of(2025, 11, 1),
                "AFTERNOON"
        );

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("update-notification"), any(Context.class));
    }

    @Test
    void shouldSendReservationCancellationEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("cancellation-notification"), any(Context.class)))
                .thenReturn("<html>Email content</html>");

        emailService.sendReservationCancellationEmail(
                "test@example.com",
                "María",
                "Room C",
                LocalDate.of(2025, 12, 5),
                "EVENING"
        );

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("cancellation-notification"), any(Context.class));
    }
}
