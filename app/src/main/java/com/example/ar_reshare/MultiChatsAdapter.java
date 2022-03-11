package com.example.ar_reshare;



import android.content.Context;
import android.content.Intent;
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
        Chat chat = (Chat) mChatList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() ,MessagingActivity.class);
                intent.putExtra("contributor", chat.getContributor());
                intent.putExtra("user",chat.getCurrentUser());
                intent.putExtra("profilePicId",chat.getContributor().getProfileIcon());

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
            chatTitle.setText(chat.getContributor().getName());
            productInfo.setText(chat.getProduct().getName());
            chatBody.setText(chat.getMessages().get(chat.getMessages().size()-1).getMessage());
            chatTime.setText(chat.getMessages().get(chat.getMessages().size()-1).getCreatedTime());
            icon.setImageResource(chat.getContributor().getProfileIcon());
        }
    }

}
