package com.example.ar_reshare;

public class User {
    String name;
    String profileUrl;
    //0 for sender, 1 for receiver
    int type;

    public User(String name, String profileUrl, int type) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getType() {
        return type;
    }
}
