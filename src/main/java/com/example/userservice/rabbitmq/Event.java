package com.example.userservice.rabbitmq;

public class Event {
    private String userEmail;
    private String message;

    public Event() {
    }

    public Event(String userEmail, String message) {
        this.userEmail = userEmail;
        this.message = message;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
