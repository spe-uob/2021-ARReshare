package com.example.ar_reshare;

public class Message {
    String message;
    User sender;
    String createdTime;

    public Message(String message, User sender, String createdTime) {
        this.message = message;
        this.sender = sender;
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}
