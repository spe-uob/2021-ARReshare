package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

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

       new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        chatListAdapter = new MultiChatsAdapter(this,mChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatListAdapter);
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
                    .setPositiveButton("yes", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ChatListActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                            //Remove swiped item from list and notify the RecyclerView
                            int position = viewHolder.getAdapterPosition();
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
                if (dX < -198){
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

}