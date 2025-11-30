package com.example.notification.service;

import com.example.notification.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;

    @Autowired
    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${app.kafka.topic.user-events:user-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-group}")
    public void consumeUserEvent(UserEvent event) {
        logger.info("Received user event: {}", event);

        try {
            switch (event.getOperation()) {
                case "CREATE":
                    emailService.sendAccountCreatedEmail(event.getEmail(), event.getUserName());
                    break;
                case "DELETE":
                    emailService.sendAccountDeletedEmail(event.getEmail(), event.getUserName());
                    break;
                default:
                    logger.warn("Unknown operation: {}", event.getOperation());
            }

            logger.info("Successfully processed user event for email: {}", event.getEmail());
        } catch (Exception e) {
            logger.error("Failed to process user event: {}", event, e);
            }
    }
}