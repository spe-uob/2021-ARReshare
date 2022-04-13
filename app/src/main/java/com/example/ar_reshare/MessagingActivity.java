package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONException;

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
    Integer conversationId;
    Integer currentUserId;
    Product product;
    Handler handler;
    Runnable refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();

        chatTextView = (EditText)findViewById(R.id.text_chatbox);
        setContentView(R.layout.message_list_layout);
        Intent i = getIntent();
        conversationId = i.getIntExtra("conversationId", -1);
        currentUserId = i.getIntExtra("currentUserId", -1);
        if (conversationId != -1){
            getConversationByID(conversationId);
        }

        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                recyclerView.scrollToPosition(mMessageList.size()-1);
            }
        });

        refresh = new Runnable() {
            public void run() {
                // Do something
                System.out.println("refresh");
                getConversationByID(conversationId);
                messageListAdapter.notifyDataSetChanged();
                handler.postDelayed(refresh,500);
            }
        };
        handler.post(refresh);

        sendButton = (ImageButton) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton = findViewById(R.id.button_send);
                chatTextView = findViewById(R.id.text_chatbox);
                String text = chatTextView.getText().toString();
                Message message1 = new Message(currentUserId,text,simpleDateFormat.format(new Date())," ", " ");
                mMessageList.add(message1);
                chatTextView.setText("");
                sendConversationMessage(conversationId,text,null);
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

        messageListAdapter = new MessageListAdapter (this,mMessageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messageListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refresh);
    }

    private void getConversationByID(Integer conversationID){

        BackendController.getConversationByID(conversationID, new BackendController.MessageBackendCallback() {

            @Override
            public void onBackendResult(boolean success, String message, int loggedInUserID,Message.MessageResult messageResult) {
                if (success) {
                    System.out.println("get conversations successful");
                    System.out.println(message);
                    messageListAdapter.setMessageResult(messageResult, loggedInUserID);
                    mMessageList.clear();
                    int resSize = messageResult.getMessages().size();
                    int mSize = mMessageList.size();
                    if (resSize > mSize){
                        int offset = resSize-mSize;
                        for (int i = resSize - 1;i>=(resSize-offset);i--){
                            mMessageList.add(messageResult.getMessages().get(i));
                            messageListAdapter.notifyDataSetChanged();
                        }
                    }
                    recyclerView.setAdapter(messageListAdapter);
                }else {
                    System.out.println(message);
                    System.out.println("fail to get conversations");
                }
            }
        });
    }


    private void sendConversationMessage(Integer conversationID, String textContent, String mediaContent){

        try {
            BackendController.sendConversationMessage(conversationID, textContent, mediaContent, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if (success) {
                        System.out.println(message);
                    }else {
                        System.out.println("fails" + message);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

