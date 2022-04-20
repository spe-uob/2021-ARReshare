package com.example.ar_reshare;

import com.google.gson.annotations.SerializedName;

public class PostcodeDetails {
    @SerializedName("postcode")
    private String postcode;
    @SerializedName("nuts")
    private String city;
    @SerializedName("latitude")
    private Float latitude;
    private Float longitude;

    public PostcodeDetails() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
