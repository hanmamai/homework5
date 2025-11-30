package com.example.notification.controller;

import com.example.notification.dto.EmailRequest;
import com.example.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Test
    void sendEmail_ShouldSendEmail() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest(
                "user@example.com",
                "Test Subject",
                "Test Message"
        );

        // When & Then
        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully"));

        verify(emailService).sendCustomEmail(
                "user@example.com",
                "Test Subject",
                "Test Message"
        );
    }

    @Test
    void sendEmail_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        EmailRequest invalidRequest = new EmailRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendCustomEmail(anyString(), anyString(), anyString());
    }
}