package com.example.service;

import com.example.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);

    @Value("${app.kafka.topic.user-events:user-events}")
    private String userEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(String email, String userName) {
        UserEvent event = new UserEvent("CREATE", email, userName);
        sendEvent(event);
    }

    public void sendUserDeletedEvent(String email, String userName) {
        UserEvent event = new UserEvent("DELETE", email, userName);
        sendEvent(event);
    }

    private void sendEvent(UserEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(userEventsTopic, event.getEmail(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent user event: {} with offset: {}",
                        event, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send user event: {} due to: {}",
                        event, ex.getMessage());
            }
        });
    }
}