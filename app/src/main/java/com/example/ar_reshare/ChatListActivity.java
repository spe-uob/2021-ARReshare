package com.example.ar_reshare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChatListActivity extends AppCompatActivity {

    MultiChatsAdapter chatListAdapter;
    RecyclerView recyclerView;
    TextView chatTitle;
    TextView chatBody;
    List<Chat> mChatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        recyclerView = findViewById(R.id.recyclerView_chats_list);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(mChatList.size() - 1);
            }
        });

        chatTitle = findViewById(R.id.chat_title);
        chatBody = findViewById(R.id.chat_body);

        getConversationDescriptors();

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        chatListAdapter = new MultiChatsAdapter(this, mChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatListAdapter);

//
        try {
            createConversation(61);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Row is swiped from recycler view
            // remove it from adapter
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setMessage("Do you want to delete?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ChatListActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                            //Remove swiped item from list and notify the RecyclerView
                            int position = viewHolder.getAdapterPosition();
                            closeConversation(mChatList.get(position).getConversationID());
                            mChatList.remove(position);
                            chatListAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chatListAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    })
                    .create()
                    .show();
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // view the background view
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            Drawable icon = getDrawable(R.drawable.delete_garbage_rubbish_trash_icon);
            ColorDrawable background = new ColorDrawable(Color.RED);
            int backgroundCornerOffset = 20;
            float translationX = dX;
            View itemView = viewHolder.itemView;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            System.out.println(dX);

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            } else if (dX < 0) { // Swiping to the left
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                if (dX < -198) {
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
                icon.setBounds(0, 0, 0, 0);
            }
        }
    };

    public static void createConversation(Integer listingID) throws JSONException {

        BackendController.createConversation(listingID, new BackendController.BackendCallback() {
            @Override
            public void onBackendResult(boolean success, String message) {
                if (success) {
                    System.out.println("conversation created");
                } else {
                    System.out.println(message);
                    System.out.println("conversation created failed");
                }
            }
        });
    }



    private void getConversationDescriptors(){

        BackendController.getConversationDescriptors(new BackendController.ChatBackendCallback() {

            @Override
            public void onBackendResult(boolean success, String message, int loggedInUserID, Chat.ConversationsResult conversationsResult) {
                if (success) {
                    System.out.println("get conversations successful");
                    //setConversations(conversations);
                    chatListAdapter.setCurrentUser(loggedInUserID);
                    //mChatList.clear();
                    for (Chat chat : conversationsResult.getChats()) {
                        if (loggedInUserID == chat.getContributorID()) {
                            getProfileIcon(chat.getReceiverID(), chat);
                        }else {
                            getProfileIcon(chat.getContributorID(), chat);
                        }
//                        addChat(chat.getConversationID(),chat);
                    }
                    //mChatList.addAll(conversationsResult.getChats());
                    //recyclerView.getAdapter().notifyDataSetChanged();
//                    for (Chat chat : conversations.getChats()){
//                        mChatList.add(chat);
//                    }
                }else {
                    System.out.println("fail to get conversations");
                }
            }
        });
    }

    private void getProfileIcon(int id, Chat chat) {

        BackendController.getProfileByID(0, 1, id, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if (success) {
                    System.out.println("successfully get profile icon");
                    //chat.setProfilePicUrl(userProfile.getProfilePicUrl());
                    downloadImage(userProfile.getProfilePicUrl(),chat);
                    //addChat(chat.getConversationID(), chat);
//                    mChatList.add(chat);
//                    recyclerView.getAdapter().notifyDataSetChanged();
                }else {
                    System.out.println("fail to get profile icon in chats");
                }
            }
        });
    }

    private void downloadImage(String url, Chat chat) {

        DownloadImageHelper.downloadImage(url, new DownloadImageHelper.ImageDownloadCallback() {
            @Override
            public void onImageDownloaded(boolean success, Bitmap image) {
                if (success) {
                    System.out.println("get chat profile icon image");
                    chat.setProfileIcon(image);
                    addChat(chat.getConversationID(), chat);
                }else {
                    System.out.println("fail to get chat profile icon image");
                }
            }
        });
    }

    private void addChat(Integer conversationID, Chat chat){

        BackendController.getConversationByID(conversationID, new BackendController.MessageBackendCallback() {

            @Override
            public void onBackendResult(boolean success, String message, int loggedInUserID, Message.MessageResult messageResult) {
                if (success) {
                    System.out.println("get lastMessage successful");
                    System.out.println(message);
                    int size = messageResult.getMessages().size();
                    //System.out.println("last message is " + lastMessage.getMessage());
                    chat.setLastMessage(messageResult.getMessages().get(size-1));
                    mChatList.add(chat);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else {
                    System.out.println(message);
                    System.out.println("fail to get lastMessage");
                }
            }
        });
    }



    private void closeConversation(Integer conversationID){
        //int conversationID = 0;

        BackendController.closeConversation(conversationID, new BackendController.BackendCallback() {
            @Override
            public void onBackendResult(boolean success, String message) {
                if (success) {
                    System.out.println("close conversation successfully");
                } else {
                    System.out.println("fail to close conversation");
                    System.out.println(message);
                }
            }
        });
    }




}