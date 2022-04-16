package com.example.ar_reshare;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Message {
    private Integer senderID;
    @SerializedName("textContent")
    private String message;
    @SerializedName("sentTime")
    private String createdTime;
    private String mediaContentMimetype;
    @SerializedName("mediaContent")
    private String imageUrl;
    private Bitmap profileIcon;

    public Message(Integer senderID, String message, String createdTime, String mediaContentMimetype, String imageUrl) {
        this.senderID = senderID;
        this.message = message;
        this.createdTime = createdTime;
       // this.mediaContentMimetype = mediaContentMimetype;
        this.imageUrl = imageUrl;
    }

    public Integer getSenderID() {
        return senderID;
    }

    public String getMediaContentMimetype() {
        return mediaContentMimetype;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public Bitmap getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(Bitmap profileIcon) {
        this.profileIcon = profileIcon;
    }

    class MessageResult{
        private Integer listingID;
        private String title;
        private String mimetype;
        private String url;
        private Integer receiverID;
        private String receiverName;
        private Integer contributorID;
        private String contributorName;
        private String closedDate;

        @SerializedName("messages")
        private List<Message> messages;

        public MessageResult() {
        }

        public MessageResult(Integer listingID, String title, String mimetype, String url,
                             Integer receiverID, String receiverName, Integer contributorID,
                             String contributorName, String closedDate, List<Message> messages) {
            this.listingID = listingID;
            this.title = title;
            this.mimetype = mimetype;
            this.url = url;
            this.receiverID = receiverID;
            this.receiverName = receiverName;
            this.contributorID = contributorID;
            this.contributorName = contributorName;
            this.closedDate = closedDate;
            this.messages = messages;
        }

        public Integer getListingID() {
            return listingID;
        }

        public String getTitle() {
            return title;
        }

        public String getMimetype() {
            return mimetype;
        }

        public String getUrl() {
            return url;
        }

        public Integer getReceiverID() {
            return receiverID;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public Integer getContributorID() {
            return contributorID;
        }

        public String getContributorName() {
            return contributorName;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public String getClosedDate() {
            return closedDate;
        }

    }


}
