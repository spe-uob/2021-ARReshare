package com.example.ar_reshare;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Product {
    private String name;
    private String description;
    private User contributor;
    private Category category;
    private String date;
    private LatLng location;
    private List<Integer> images;

    Product(String name, String description, User contributor, Category category, double lat, double lng) {
        this.name = name;
        this.description = description;
        this.contributor = contributor;
        this.category = category;
        this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        this.location = new LatLng(lat,lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getContributor() {
        return contributor;
    }

    public void setContributor(User contributor) {
        this.contributor = contributor;
    }

    public Category getCategory() {return category;}

    public void setCategory(Category category) {this.category = category;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public List<Integer> getImages() {
        return images;
    }

    public void addImages(Integer image) {
        this.images.add(image);
    }
}
