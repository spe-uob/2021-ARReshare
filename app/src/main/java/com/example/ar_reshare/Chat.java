package com.example.ar_reshare;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chat {

    private Integer conversationID;
    @SerializedName("listingID")
    private Integer productID;
    @SerializedName("title")
    private String productName;
    private Integer receiverID;
    private String receiverName;
    private String url;
    private Integer contributorID;
    private String contributorName;
    private User currentUser;
    private User contributor;
    private List<Message> messages;
    private Product product;

    public Chat(Integer conversationID, Integer productID,
                String productName, Integer receiverID, String receiverName, String url,
                Integer contributorID, String contributorName) {
        this.conversationID = conversationID;
        this.productID = productID;
        this.productName = productName;
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.url = url;
        this.contributorID = contributorID;
        this.contributorName = contributorName;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getContributor() {
        return contributor;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Product getProduct() {
        return product;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public Integer getConversationID() {
        return conversationID;
    }

    public Integer getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getUrl() {
        return url;
    }

    public Integer getContributorID() {
        return contributorID;
    }

    public String getContributorName() {
        return contributorName;
    }

    class ConversationsResult {
        @SerializedName("conversations")
        private List<Chat> chats;
        Integer success;

        public ConversationsResult() {
        }

        public ConversationsResult(List<Chat> chats, Integer success) {
            this.chats = chats;
            this.success = success;
        }


        public List<Chat> getChats() {
            return chats;
        }

        public Integer getSuccess() {
            return success;
        }
    }
}
