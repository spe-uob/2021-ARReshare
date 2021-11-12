package com.example.ar_reshare;

public class Product {
    private String name;
    private String description;
    private String contributor;

    public Product(String name, String description, String contributor) {
        this.name = name;
        this.description = description;
        this.contributor = contributor;
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
}
