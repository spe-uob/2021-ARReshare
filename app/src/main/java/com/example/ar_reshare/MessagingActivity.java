package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
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

    class ButtonActivity implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String text = chatTextView.getText().toString();
            MessageListAdapter.Message message1 = new MessageListAdapter.Message(text, new MessageListAdapter.User("david", ""),
                    simpleDateFormat.format(new Date()));
            mMessageList.add(message1);
        }
    }

    public void sendMessage(View view){
        sendButton = findViewById(R.id.button_send);
        chatTextView = findViewById(R.id.text_chatbox);
        String text = chatTextView.getText().toString();
        MessageListAdapter.Message message1 = new MessageListAdapter.Message(text, new MessageListAdapter.User("david", ""),
                simpleDateFormat.format(new Date()));
        mMessageList.add(message1);
        recyclerView.setAdapter(messageListAdapter);
        chatTextView.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageListAdapter.User user = new MessageListAdapter.User("david", "");
//        MessageListAdapter.Message message = new MessageListAdapter.Message("hi!!!", user, simpleDateFormat.format(new Date()));
//        mMessageList.add(message);
//        MessageListAdapter.Message message2 = new MessageListAdapter.Message("how are you!!!", user, simpleDateFormat.format(new Date()));
//        mMessageList.add(message2);
        sendButton = findViewById(R.id.button_send);
        chatTextView = findViewById(R.id.text_chatbox);
        //sendButton.setOnClickListener(new ButtonActivity());
//        sendButton.setOnClickListener(v -> {
//            String text = chatTextView.getText().toString();
//            MessageListAdapter.Message message1 = new MessageListAdapter.Message(text, new MessageListAdapter.User("david", ""),
//                    simpleDateFormat.format(new Date()));
//            mMessageList.add(message1);
//        });



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
        //((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);
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

