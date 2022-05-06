package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    Context mContext;
    Integer conversationId;
    Integer currentUserId;
    Integer listingId;
    Integer contributorId;
    Product product;
    Handler handler;
    Runnable refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.handler = new Handler();

        setContentView(R.layout.message_list_layout);
        Intent i = getIntent();
        listingId = i.getIntExtra("listingId", -1);
        conversationId = i.getIntExtra("conversationId", -1);
        currentUserId = i.getIntExtra("currentUserId", -1);
        contributorId = i.getIntExtra("contributorId", -1);

        chatTextView = (EditText)findViewById(R.id.text_chatbox);
        recyclerView = findViewById(R.id.reyclerview_message_list);

        if (conversationId != -1 && currentUserId != -1){
            if (BackendController.getLoggedInUserID() == contributorId) {
                getProfileById(conversationId, contributorId);
            }else {
                getProfileById(conversationId, currentUserId);
            }
        }

        setLayOutChangeListener();

        sendButton = (ImageButton) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton = (ImageButton) findViewById(R.id.button_send);
                chatTextView = findViewById(R.id.text_chatbox);
                String text = chatTextView.getText().toString();
                Message message1 = new Message(currentUserId,text,simpleDateFormat.format(new Date())," ");
                mMessageList.add(message1);
                chatTextView.setText("");
                sendConversationMessage(conversationId,text,null);
            }
        });

        setOnTouchListener();

        messageListAdapter = new MessageListAdapter (mContext,mMessageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(messageListAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refresh);
    }

    private void setOnTouchListener() {
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
    }

    private void setLayOutChangeListener() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                recyclerView.scrollToPosition(mMessageList.size()-1);
            }
        });
    }

    private void getProfileById(Integer conversationId, Integer contributorId) {

        BackendController.getProfileByID(0, 1, contributorId, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if (success) {
                    downloadImage(userProfile.getProfilePicUrl(), conversationId);
                }else {
                    System.out.println("fail to get user profile");
                }
            }
        });
    }

    private void getConversationByID(Integer conversationID, Bitmap image){

        BackendController.getConversationByID(conversationID, new BackendController.MessageBackendCallback() {

            @Override
            public void onBackendResult(boolean success, String message, int loggedInUserID,Message.MessageResult messageResult) {
                if (success) {
                    System.out.println("get conversations successful");
                    System.out.println(message);
                }else {
                    System.out.println(message);
                    System.out.println("fail to get conversations");
                }
                messageListAdapter.setMessageResult(messageResult, loggedInUserID);
                mMessageList.clear();
                int resSize = messageResult.getMessages().size();
                int mSize = mMessageList.size();
                if (resSize > mSize){
                    int offset = resSize-mSize;
                    List<Message> messages = new ArrayList<>();
                    for (int i = resSize - 1;i>=(resSize-offset);i--){
                        messageResult.getMessages().get(i).setProfileIcon(image);
                        mMessageList.add(messageResult.getMessages().get(i));
                        messageListAdapter.notifyDataSetChanged();
                    }
                }
                recyclerView.setAdapter(messageListAdapter);
            }
        });
    }


    private void downloadImage(String url, Integer conversationId) {

        DownloadImageHelper.downloadImage(url, new DownloadImageHelper.ImageDownloadCallback() {
            @Override
            public void onImageDownloaded(boolean success, Bitmap image) {
                if (success) {
                    System.out.println("get message profile icon image");
                    getConversationByID(conversationId, image);
                    refresh = new Runnable() {
                        public void run() {
                            // Do something
                            System.out.println("refresh");
                            getConversationByID(conversationId, image);
                            messageListAdapter.notifyDataSetChanged();
                            handler.postDelayed(refresh,500);
                        }
                    };
                    handler.post(refresh);
                }else {
                    System.out.println("fail to get message profile icon image");
                    getConversationByID(conversationId, BitmapFactory.decodeResource(null, R.mipmap.ic_launcher_round));
                    refresh = new Runnable() {
                        public void run() {
                            // Do something
                            System.out.println("refresh");
                            getConversationByID(conversationId, BitmapFactory.decodeResource(null, R.mipmap.ic_launcher_round));
                            messageListAdapter.notifyDataSetChanged();
                            handler.postDelayed(refresh,500);
                        }
                    };
                    handler.post(refresh);
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


    //    convert to date as follows
//    dates[0] day name(Mon, Tue, etc)
//    dates[1] Month name
//    dates[2] day number
//    dates[3] time (hh:mm)
//    dates[4] time zone
//    dates[5] year number
    public static String[] convertDate (String dateString) {
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(dateString);
        Instant i = Instant.from(ta);
        Date date = Date.from(i);

        String[] dates = date.toString().split(" ");
        String time = dates[3].substring(0,5);
        dates[3] = time;

        return dates;
    }



}