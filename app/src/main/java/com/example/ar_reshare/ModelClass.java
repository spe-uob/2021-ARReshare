package com.example.ar_reshare;

public class ModelClass {

    private int profileIcon;
    private int productImage;
    private String productTitle;
    private String productDescription;

    public ModelClass(int profileIcon, int productImage, String productTitle, String productDescription) {
        this.profileIcon = profileIcon;
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
