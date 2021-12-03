package com.example.ar_reshare;

public class User {
    String nickname;
    String profileUrl;
    //0 for sender, 1 for receiver
    int type;

    public User(String nickname, String profileUrl, int type) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getType() {
        return type;
    }
}
