package com.example.ar_reshare;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

public class Product implements Parcelable {
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
        this.images = new ArrayList<>();
    }

    protected Product(Parcel in) {
        name = in.readString();
        description = in.readString();
        date = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }
    //CREATOR for Parcelable items
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    //implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeParcelable(location, flags);
    }
}
