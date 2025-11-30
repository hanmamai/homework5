package com.example.notification.service;

import com.example.notification.dto.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "app.kafka.topic.user-events=user-events"
})
class UserEventConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void consumeUserEvent_ShouldProcessCreateEvent() throws Exception {
        // Given
        UserEvent createEvent = new UserEvent("CREATE", "john@example.com", "John Doe");

        // When
        kafkaTemplate.send("user-events", "john@example.com", createEvent);

        // Then
        verify(emailService, timeout(5000))
                .sendAccountCreatedEmail("john@example.com", "John Doe");
    }

    @Test
    void consumeUserEvent_ShouldProcessDeleteEvent() throws Exception {
        // Given
        UserEvent deleteEvent = new UserEvent("DELETE", "jane@example.com", "Jane Smith");

        // When
        kafkaTemplate.send("user-events", "jane@example.com", deleteEvent);

        // Then
        verify(emailService, timeout(5000))
                .sendAccountDeletedEmail("jane@example.com", "Jane Smith");
    }
}