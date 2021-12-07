package com.example.ar_reshare;

import com.google.android.gms.maps.model.LatLng;

public class Account {
    private String id;
    private String email;
    private String dateOfBirth;
    private String password;
    private String address;
    private LatLng lastKnownLocation;
    private User user;

    public Account(String id, String email, String dateOfBirth, String password, String address, User user) {
        this.id = id;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.address = address;
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

    public LatLng getLiveLocation() {
        return lastKnownLocation;
    }

    public void setLiveLocation(LatLng lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public User getUser() {
        return user;
    }
}
