package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                recyclerView.scrollToPosition(mChatList.size()-1);
            }
        });

        chatTitle = findViewById(R.id.chat_title);
        chatBody = findViewById(R.id.chat_body);
        for (Chat chat : ExampleData.getChats()){
            mChatList.add(chat);
        }


        chatListAdapter = new MultiChatsAdapter(this,mChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(chatListAdapter);
    }
}