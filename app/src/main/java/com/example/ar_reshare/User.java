package com.example.ar_reshare;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String name;
    String profileUrl;
    //0 for sender, 1 for receiver
    int messengerType;
    String bio;

    public User(String name, String profileUrl, int messengerType) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.messengerType = messengerType;
    }

    protected User(Parcel in) {
        name = in.readString();
        profileUrl = in.readString();
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

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getMessengerType() {
        return messengerType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(profileUrl);
        dest.writeInt(messengerType);
        dest.writeString(bio);
    }
}
