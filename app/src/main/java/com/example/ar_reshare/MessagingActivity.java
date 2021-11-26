package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagingActivity extends AppCompatActivity{

    MessageListAdapter messageListAdapter;
    RecyclerView recyclerView;
    Button sendButton;
    EditText chatTextView;
    List<MessageListAdapter.Message> mMessageList = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");


    public void sendMessage(View view){
        sendButton = findViewById(R.id.button_send);
        chatTextView = findViewById(R.id.text_chatbox);
        String text = chatTextView.getText().toString();
        MessageListAdapter.User user1 = messageListAdapter.new User("david", "", 1);
        MessageListAdapter.Message message1 = messageListAdapter.new Message(text, messageListAdapter.new User("david", "", 0),
                simpleDateFormat.format(new Date()));
        mMessageList.add(message1);
        if (mMessageList.size() == 1) {
            MessageListAdapter.Message message2 = messageListAdapter.new Message("hi", user1, simpleDateFormat.format(new Date()));
            mMessageList.add(message2);
        }else if (mMessageList.size() == 3){
            MessageListAdapter.Message message3 = messageListAdapter.new Message("how are you  ", user1, simpleDateFormat.format(new Date()));
            mMessageList.add(message3);
        }else if(mMessageList.size() == 5) {
            MessageListAdapter.Message message4 = messageListAdapter.new Message("do you want to share anything?  ", user1, simpleDateFormat.format(new Date()));
            mMessageList.add(message4);
        }else if (mMessageList.size() == 7) {
            MessageListAdapter.Message message5 = messageListAdapter.new Message("thank you! bye bye 🤭 ", user1, simpleDateFormat.format(new Date()));
            mMessageList.add(message5);
        }else {
            MessageListAdapter.Message message6 = messageListAdapter.new Message("I cannot come up with more words 😟", user1, simpleDateFormat.format(new Date()));
            mMessageList.add(message6);
        }

        recyclerView.setAdapter(messageListAdapter);
        chatTextView.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendButton = findViewById(R.id.button_send);
        chatTextView = findViewById(R.id.text_chatbox);
        setContentView(R.layout.message_list_layout);


        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                recyclerView.scrollToPosition(mMessageList.size()-1);
            }
        });


        messageListAdapter = new MessageListAdapter(this,mMessageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messageListAdapter);
    }




//    @Override
//    public void onClick(View v) {
//        String text = chatTextView.getText().toString();
//        MessageListAdapter.Message message1 = new MessageListAdapter.Message(text, new MessageListAdapter.User("david", ""),
//                simpleDateFormat.format(new Date()));
//        mMessageList.add(message1);
//        recyclerView.setAdapter(messageListAdapter);
//    }
}

