package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChatListActivity extends Fragment implements NavigationBarView.OnItemSelectedListener {

    MultiChatsAdapter chatListAdapter;
    RecyclerView recyclerView;
    TextView chatTitle;
    TextView chatBody;
    List<Chat> mChatList = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_chats_list);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(mChatList.size() - 1);
            }
        });
        chatTitle = view.findViewById(R.id.chat_title);
        chatBody = view.findViewById(R.id.chat_body);

        mChatList.clear();
        getConversationDescriptors();

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        chatListAdapter = new MultiChatsAdapter(getActivity(), mChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatListAdapter);
        return view;
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
                            Toast.makeText(getActivity(), "Deleted ", Toast.LENGTH_SHORT).show();
                            //Remove swiped item from list and notify the RecyclerView
                            int position = viewHolder.getAdapterPosition();
                            try {
                                closeConversation(mChatList.get(position).getConversationID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            Drawable icon = getResources().getDrawable(R.drawable.delete_garbage_rubbish_trash_icon, null);
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

    private void createConversation(Integer listingID) throws JSONException {

        BackendController.createConversation(listingID, new BackendController.BackendCallback() {
            @Override
            public void onBackendResult(boolean success, String message) {
                if (success) {
                    System.out.println("conversation created");
                    Integer conversationId = Integer.valueOf(message);
                    // Intent intent = new Intent(, MessagingActivity.class);
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
                    chatListAdapter.setCurrentUser(loggedInUserID);
                    for (Chat chat : conversationsResult.getChats()) {
                        if (loggedInUserID == chat.getContributorID()) {
                            getProfileIcon(chat.getReceiverID(), chat);
                        }else {
                            getProfileIcon(chat.getContributorID(), chat);
                        }
                    }
                }else {
                    System.out.println("fail to get conversations");
                }
            }
        });
    }

    private void getProfileIcon(int id, Chat chat) {
        System.out.println("received id in chat is " + id);

        BackendController.getProfileByID(0, 1, id, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if (success) {
                    System.out.println("successfully get profile icon");
                    chat.setProfilerUrl(userProfile.getProfilePicUrl());
                    downloadImage(userProfile.getProfilePicUrl(),chat);
                }else {
                    System.out.println("fail to get profile icon in chats");
                    chat.setProfileIcon(BitmapFactory.decodeResource(null, R.mipmap.ic_launcher_round));
                    addChat(chat.getConversationID(), chat);
                }
            }
        });
    }

    private void downloadImage(String url, Chat chat) {

        DownloadImageHelper.downloadImage(url, new DownloadImageHelper.ImageDownloadCallback() {
            @Override
            public void onImageDownloaded(boolean success, Bitmap image) {
                if (success) {
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
                    System.out.println(message);
                    int size = messageResult.getMessages().size();
                    if (messageResult.getMessages().size() > 0){
                        chat.setLastMessage(messageResult.getMessages().get(0));
                    } else {
                        chat.setLastMessage(null);
                    }
                    mChatList.add(chat);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else {
                    System.out.println(message);
                }
            }
        });
    }



    public static void closeConversation(Integer conversationID) throws JSONException {
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.map_menu_item:

                System.out.println("in map");
                intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, mapsActivity).commit();
                return true;

            case R.id.feed_menu_item:
                intent = new Intent(getActivity(), FeedActivity.class);
                startActivity(intent);
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, secondFragment).commit();
                return true;

            case R.id.ar_menu_item:
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, thirdFragment).commit();
                return true;
        }
        return false;
    }
}