package com.example.ar_reshare;

import java.util.List;

public class Chat {
    private User currentUser;
    private User contributor;
    private List<Message> messages;
    private Product product;


    public Chat(User currentUser, User contributor, List<Message> messages, Product product) {
        this.currentUser = currentUser;
        this.contributor = contributor;
        this.messages = messages;
        this.product = product;
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

    public Product getProduct() {
        return product;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
