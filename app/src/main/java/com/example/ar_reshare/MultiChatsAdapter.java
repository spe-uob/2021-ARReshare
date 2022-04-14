package com.example.ar_reshare;



import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MultiChatsAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Chat> mChatList;
    private Chat resChat;
    private int loggedInUserID;
    private Message lastMessage;

    public MultiChatsAdapter(Context context, List<Chat> chatList) {
        mContext = context;
        mChatList = chatList;
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_chat_layout, parent, false);
        return new MultiChatsAdapter.ChatHolder(view);

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = mChatList.get(position);
        //System.out.println("chat id is" + chat.getConversationID());
        Integer index = mChatList.get(position).getConversationID();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() ,MessagingActivity.class);
                intent.putExtra("conversationId", index);
                intent.putExtra("currentUserId", chat.getReceiverID());
//                intent.putExtra("contributor", chat.getContributor());
//                intent.putExtra("user",chat.getCurrentUser());
//                intent.putExtra("profilePicId",chat.getContributor().getProfileIcon());
                mContext.startActivity(intent);
            }
        });
        ((MultiChatsAdapter.ChatHolder) holder).bind(chat);
    }



    private class ChatHolder extends RecyclerView.ViewHolder {
        TextView chatTitle, chatBody, chatTime, productInfo;
        ImageView icon;

        ChatHolder(View itemView) {
            super(itemView);

            chatTitle = (TextView) itemView.findViewById(R.id.chat_title);
            chatBody = (TextView) itemView.findViewById(R.id.chat_body);
            chatTime = (TextView) itemView.findViewById(R.id.chat_time);
            icon = (ImageView) itemView.findViewById(R.id.chat_icon);
            productInfo = (TextView) itemView.findViewById(R.id.chat_product);
        }

        void bind(Chat chat) {
            if (loggedInUserID == chat.getContributorID()) {
                chatTitle.setText(chat.getReceiverName());
                productInfo.setText(chat.getProductName());
            }else {
                chatTitle.setText(chat.getContributorName());
                productInfo.setText(chat.getProductName());
            }
            if (chat.getLastMessage() == null) {
                chatBody.setText(" ");
            }else {
                chatBody.setText(chat.getLastMessage().getMessage());
            }
            //chatTime.setText(chat.getLastMessage().getCreatedTime());
//            if (chat.getMessages().size() > 0) {
//                chatBody.setText(chat.getMessages().get(chat.getMessages().size()-1).getMessage());
//            }else {
//
//            }

//            chatTime.setText(chat.getMessages().get(chat.getMessages().size()-1).getCreatedTime());
//            icon.setImageResource(chat.getContributor().getProfileIcon());
        }
    }

    public void setCurrentUser(int loggedInUserID){
        this.loggedInUserID = loggedInUserID;
    }

//    public void setLastMessage(Message message) {this.lastMessage = message;}

//    private void bindChat(Integer conversationID, RecyclerView.ViewHolder holder, Chat chat){
//        System.out.println("in getLastMessage");
//
//        BackendController.getConversationByID(conversationID, new BackendController.MessageBackendCallback() {
//
//            @Override
//            public void onBackendResult(boolean success, String message, int loggedInUserID, Message.MessageResult messageResult) {
//                if (success) {
//                    System.out.println("get lastMessage successful");
//                    System.out.println(message);
//                    int size = messageResult.getMessages().size();
//                    chat.setLastMessage(messageResult.getMessages().get(size-1));
//                }else {
//                    System.out.println(message);
//                    System.out.println("fail to get lastMessage");
//                }
//            }
//        });
//    }

}
