package com.example.ar_reshare;

import java.util.List;

public class Chat {
    private User currentUser;
    private User contributor;
    private List<Message> messages;


    public Chat(User currentUser, User contributor, List<Message> messages) {
        this.currentUser = currentUser;
        this.contributor = contributor;
        this.messages = messages;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getContributor() {
        return contributor;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
