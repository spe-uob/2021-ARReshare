package com.example.ar_reshare;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Product {
    private String name;
    private String description;
    private String contributor;
    private String date;

    public Product(String name, String description, String contributor) {
        this.name = name;
        this.description = description;
        this.contributor = contributor;
        this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
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

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
