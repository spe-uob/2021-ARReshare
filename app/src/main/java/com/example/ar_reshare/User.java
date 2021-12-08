package com.example.ar_reshare;

import java.io.Serializable;

public class User implements Serializable{
    String name;
    String profileUrl;
    int profileIcon;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon) {
        this.profileIcon = profileIcon;
    }

    public int getMessengerType() {
        return messengerType;
    }
}
