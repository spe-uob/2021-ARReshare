package com.example.ar_reshare;



import static androidx.camera.core.CameraX.getContext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.List;

public class MultiChatsAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Chat> mChatList;
    private Chat resChat;
    private int loggedInUserID;
    private Message lastMessage;
    private MultiChatsAdapter multiChatsAdapter = this;

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
        Integer index = mChatList.get(position).getConversationID();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() ,MessagingActivity.class);
                intent.putExtra("conversationId", index);
                intent.putExtra("currentUserId", chat.getReceiverID());
                intent.putExtra("contributorId", chat.getContributorID());
                mContext.startActivity(intent);
            }
        });
        ((MultiChatsAdapter.ChatHolder) holder).bind(chat);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.menuStyle);
                PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, v.findViewById(R.id.chat_body));
                popupMenu.setGravity(Gravity.END);
                popupMenu.inflate(R.menu.chat_options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.chat_option1:
                                System.out.println("in clicking");
                                new AlertDialog.Builder(holder.itemView.getContext())
                                        .setMessage("Do you want to delete?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(mContext, "Deleted ", Toast.LENGTH_SHORT).show();
                                                //Remove swiped item from list and notify the RecyclerView
                                                int position = holder.getAdapterPosition();
                                                try {
                                                    ChatListActivity.closeConversation(mChatList.get(position).getConversationID());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                mChatList.remove(position);
                                                multiChatsAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                multiChatsAdapter.notifyItemChanged(holder.getAdapterPosition());
                                            }
                                        })
                                        .create()
                                        .show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
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
                productInfo.setText("My " + chat.getProductName() + "->");
            }else {
                chatTitle.setText(chat.getContributorName());
                productInfo.setText("<-" + chat.getProductName());
            }
            if (chat.getLastMessage() == null) {
                chatBody.setText(" ");
                chatTime.setText(" ");
            }else {
                chatBody.setText(getMinWords(chat.getLastMessage().getMessage(), 45));
                String[] dates = MessagingActivity.convertDate(chat.getLastMessage().getCreatedTime());
                chatTime.setText(dates[3]);
            }
            icon.setImageBitmap(chat.getProfileIcon());
        }
    }

    public static String getMinWords(String input, int limit) {
        int wordsLimit = limit - 4;
        String[] words = input.split(" ");
        String displayMsg = "";
        for (String s : words) {
            if (displayMsg.length() + s.length() <= wordsLimit) {
                displayMsg = displayMsg + " " + s;
            }else {
                break;
            }
        }
        System.out.println("display string is " + displayMsg);
        return displayMsg + " ...";
    }

    public void setCurrentUser(int loggedInUserID){
        this.loggedInUserID = loggedInUserID;
    }


}