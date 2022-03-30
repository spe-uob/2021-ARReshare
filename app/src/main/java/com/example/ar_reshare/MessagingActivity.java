package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity{

    MessageListAdapter messageListAdapter;
    RecyclerView recyclerView;
    ImageButton sendButton;
    EditText chatTextView;
    List<Message> mMessageList = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
    User user;
    User contributor;
    int profilePicId;
    Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatTextView = (EditText)findViewById(R.id.text_chatbox);
        setContentView(R.layout.message_list_layout);
        Intent i = getIntent();
        user = i.getParcelableExtra("user");
        contributor = i.getParcelableExtra("contributor"); // the contributor of the current product
        for (User user : ExampleData.getUsers()) {
            if (contributor.getName().equals(user.getName())) {
                contributor = user;
            }
        }
        profilePicId = i.getIntExtra("profilePicId",0);
        contributor.setProfileIcon(profilePicId);

        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                recyclerView.scrollToPosition(mMessageList.size()-1);
            }
        });

        for (Chat chat : ExampleData.getChats()) {
            if (chat.getCurrentUser().getName().equals(user.getName()) && chat.getContributor().getName().equals(contributor.getName())){
                for (Message message : chat.getMessages()) {
                    mMessageList.add(message);
                }
            }
        }

        sendButton = (ImageButton) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton = findViewById(R.id.button_send);
                chatTextView = findViewById(R.id.text_chatbox);
                String text = chatTextView.getText().toString();
//                Message message1 = new Message(text, user, simpleDateFormat.format(new Date()));
//                mMessageList.add(message1);
//                if (mMessageList.size() == 1) {
//                    Message message2 = new Message("hi", contributor, simpleDateFormat.format(new Date()));
//                    mMessageList.add(message2);
//                }else if (mMessageList.size() == 3){
//                    Message message3 = new Message("yep, it is still available!", contributor, simpleDateFormat.format(new Date()));
//                    mMessageList.add(message3);
//                }else if(mMessageList.size() == 5) {
//                    Message message4 = new Message("sure, that would work great!", contributor, simpleDateFormat.format(new Date()));
//                    mMessageList.add(message4);
//                }else if (mMessageList.size() == 7) {
//                    Message message5 = new Message("stay safe and have a good one 🤭", contributor, simpleDateFormat.format(new Date()));
//                    mMessageList.add(message5);
//                }else {
//                    Message message6 = new Message("okay", contributor, simpleDateFormat.format(new Date()));
//                    mMessageList.add(message6);
//                }

                recyclerView.setAdapter(messageListAdapter);
                chatTextView.setText("");
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatTextView = (EditText)findViewById(R.id.text_chatbox);
                chatTextView.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        messageListAdapter = new MessageListAdapter(this,mMessageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messageListAdapter);
    }


}

