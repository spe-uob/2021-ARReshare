package com.example.ar_reshare;

public class User {
    String name;
    String profileUrl;
    //0 for sender, 1 for receiver
    int messengerType;
    String bio;

    public User(String name, String profileUrl, int messengerType) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.messengerType = messengerType;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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
