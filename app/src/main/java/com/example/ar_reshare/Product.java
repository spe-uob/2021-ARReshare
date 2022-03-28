package com.example.ar_reshare;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Product implements Parcelable {
    @SerializedName("listingID")
    private int id;
    @SerializedName("title")
    private String name;
    @SerializedName("description")
    private String description;
    private User contributor;
    @SerializedName("categoryID")
    private int categoryID;
    private Category category;
    @SerializedName("creationDate")
    private String date;
    @SerializedName("postcode")
    private String postcode;
    private LatLng location;

    @SerializedName("media")
    private List<ProductMedia> productMedia;

    Product(){}

    Product(String name, String description, User contributor, Category category, double lat, double lng) {
        this.name = name;
        this.description = description;
        this.contributor = contributor;
        this.category = category;
        this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        this.location = new LatLng(lat,lng);
        this.productMedia = new ArrayList<>();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public List<ProductMedia> getProductMedia() {
        return productMedia;
    }

    public void setProductMedia(List<ProductMedia> productMedia) {
        this.productMedia = productMedia;
    }

    public List<Integer> getImages() {
        return new ArrayList<Integer>();
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

    public static class ProductMedia {
        @SerializedName("url")
        String url;
        @SerializedName("mimetype")
        String mimetype;

        public ProductMedia(){}

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMimetype() {
            return mimetype;
        }

        public void setMimetype(String mimetype) {
            this.mimetype = mimetype;
        }
    }

    public static class SearchResults {

        @SerializedName("listings")
        List<Product> searchedProducts;

        public SearchResults(){}

        public List<Product> getSearchedProducts() {
            return searchedProducts;
        }

        public void setSearchedProducts(List<Product> searchedProducts) {
            this.searchedProducts = searchedProducts;
        }
    }
}
