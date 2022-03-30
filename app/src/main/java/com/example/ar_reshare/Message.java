package com.example.ar_reshare;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Message {
    private Integer senderID;
    @SerializedName("textContent")
    private String message;
    private User sender;
    @SerializedName("sentTime")
    private String createdTime;
    private String mediaContentMimetype;
    @SerializedName("mediaContent")
    private String imageUrl;

    public Message(Integer senderID, String message, User sender, String createdTime, String mediaContentMimetype, String imageUrl) {
        this.senderID = senderID;
        this.message = message;
        this.sender = sender;
        this.createdTime = createdTime;
        this.mediaContentMimetype = mediaContentMimetype;
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

    public User getSender() {
        return sender;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    class MessageResult{
//        private Integer listingID;
//        private String title;
//        private String mimetype;
//        private String url;
        private Chat chat;
        private List<Message> messages;
        private String closedDate;

        public MessageResult() {
        }

        public MessageResult(Chat chat, List<Message> messages, String closedDate) {
            this.chat = chat;
            this.messages = messages;
            this.closedDate = closedDate;
        }

        public Chat getChat() {
            return chat;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public String getClosedDate() {
            return closedDate;
        }
    }


}
