package com.example.ar_reshare;

public class User {
    String name;
    String profileUrl;
    //0 for sender, 1 for receiver
    int messengerType;

    public User(String name, String profileUrl, int messengerType) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.messengerType = messengerType;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getMessengerType() {
        return messengerType;
    }
}
