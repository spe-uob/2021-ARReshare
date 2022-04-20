package com.example.ar_reshare;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class User implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    String profilePicUrl;
    @SerializedName("mimetype")
    String mimetype;
    @SerializedName("listings")
    List<Product> listings;
    private String uniqueProfileUrl;
    private Bitmap profilePic;
    private int profileIcon;
    //0 for sender, 1 for receiver
    private int messengerType;
    private String bio;

    public User(){}

    public User(String name, String profileUrl, int messengerType) {
        this.name = name;
        this.uniqueProfileUrl = profileUrl;
        this.messengerType = messengerType;
    }

    protected User(Parcel in) {
        name = in.readString();
        uniqueProfileUrl = in.readString();
        profileIcon = in.readInt();
        messengerType = in.readInt();
        bio = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueProfileUrl() {
        return uniqueProfileUrl;
    }

    public int getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon) {
        this.profileIcon = profileIcon;
    }

    public int getMessengerType() {
        return messengerType;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public List<Product> getListings() {
        return listings;
    }

    public void setListings(List<Product> listings) {
        this.listings = listings;
    }

    public Bitmap getProfilePic() { return profilePic; }

    public void setProfilePic(Bitmap profilePic) { this.profilePic = profilePic; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uniqueProfileUrl);
        dest.writeInt(profileIcon);
        dest.writeInt(messengerType);
        dest.writeString(bio);
    }

    public void downloadProfilePicture(CountDownLatch latch) {
        DownloadImageHelper.downloadImage(getProfilePicUrl(), (success, image) -> {
            if (success) {
                System.out.println("RECEIVED PFP SUCCESS CALLBACK");
                setProfilePic(image);
            } else {
                System.out.println("RECEIVED PFP FAILURE CALLBACK");
                setProfilePic(null);
            }
            latch.countDown();
        });
    }
}
