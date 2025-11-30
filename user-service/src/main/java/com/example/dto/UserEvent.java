package com.example.dto;

import java.time.LocalDateTime;

public class UserEvent {
    private String operation;
    private String email;
    private String userName;
    private LocalDateTime timestamp;


    public UserEvent() {}

    public UserEvent(String operation, String email, String userName) {
        this.operation = operation;
        this.email = email;
        this.userName = userName;
        this.timestamp = LocalDateTime.now();
    }


    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "UserEvent{" +
                "operation='" + operation + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}