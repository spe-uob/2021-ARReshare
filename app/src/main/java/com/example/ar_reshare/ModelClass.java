package com.example.ar_reshare;

public class ModelClass {

    private int profileIcon;
    private String contributor;
    private int productImage;
    private String productTitle;
    private String productDescription;

    public ModelClass(int profileIcon, String contributor, int productImage, String productTitle, String productDescription) {
        this.profileIcon = profileIcon;
        this.contributor = contributor;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
    }

    public int getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon) {
        this.profileIcon = profileIcon;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
