package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    MessageListAdapter messageListAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<MessageListAdapter.Message> mMessageList = new ArrayList<>();
        MessageListAdapter.User user = new MessageListAdapter.User("david", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        MessageListAdapter.Message message = new MessageListAdapter.Message("hi!!!", user, simpleDateFormat.format(new Date()));
        mMessageList.add(message);


        setContentView(R.layout.message_list_layout);

        recyclerView = findViewById(R.id.reyclerview_message_list);
        messageListAdapter = new MessageListAdapter(this,mMessageList);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageListAdapter);
    }


}

