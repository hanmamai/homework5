package com.example.notification.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=3025",
        "spring.mail.username=test@example.com",
        "spring.mail.password=test",
        "app.email.from=test@example.com"
})
class EmailServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@example.com", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @Test
    void sendAccountCreatedEmail_ShouldSendEmail() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String userName = "John Doe";

        // When
        emailService.sendAccountCreatedEmail(toEmail, userName);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Добро пожаловать!", message.getSubject());
        assertEquals("user@example.com", message.getAllRecipients()[0].toString());
        assertTrue(message.getContent().toString().contains("Ваш аккаунт на сайте"));
    }

    @Test
    void sendAccountDeletedEmail_ShouldSendEmail() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String userName = "John Doe";

        // When
        emailService.sendAccountDeletedEmail(toEmail, userName);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Ваш аккаунт был удален", message.getSubject());
        assertTrue(message.getContent().toString().contains("Ваш аккаунт был удалён"));
    }

    @Test
    void sendCustomEmail_ShouldSendEmail() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String subject = "Test Subject";
        String messageText = "Test Message";

        // When
        emailService.sendCustomEmail(toEmail, subject, messageText);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test Message", message.getContent().toString());
    }
}