package com.example.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.email.from:no-reply@example.com}")
    private String fromEmail;

    @Value("${app.email.website:http://localhost:3000}")
    private String websiteUrl;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountCreatedEmail(String toEmail, String userName) {
        String subject = "Добро пожаловать!";
        String message = String.format(
                "Здравствуйте, %s! Ваш аккаунт на сайте %s был успешно создан.",
                userName, websiteUrl
        );

        sendEmail(toEmail, subject, message);
    }

    public void sendAccountDeletedEmail(String toEmail, String userName) {
        String subject = "Ваш аккаунт был удален";
        String message = String.format(
                "Здравствуйте, %s! Ваш аккаунт был удалён.",
                userName
        );

        sendEmail(toEmail, subject, message);
    }

    public void sendCustomEmail(String toEmail, String subject, String message) {
        sendEmail(toEmail, subject, message);
    }

    private void sendEmail(String toEmail, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(toEmail);
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);

            logger.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}