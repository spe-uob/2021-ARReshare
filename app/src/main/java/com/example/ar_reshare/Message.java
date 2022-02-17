package com.example.ar_reshare;

public class Message {
    private String message;
    private User sender;
    private String createdTime;
    private String imageUrl;

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
