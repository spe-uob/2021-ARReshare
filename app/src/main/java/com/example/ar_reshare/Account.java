package com.example.ar_reshare;

public class Account {
    private String id;
    private String email;
    private String dateOfBirth;
    private String password;
    private String address;
    private String liveLocation;
    private User user;

    public Account(String id, String email, String password, User user) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLiveLocation() {
        return liveLocation;
    }

    public void setLiveLocation(String liveLocation) {
        this.liveLocation = liveLocation;
    }
}
