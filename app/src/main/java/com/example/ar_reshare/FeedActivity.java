package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends AppCompatActivity {

    ArrayList<Product> arrayList = new ArrayList<>();
    List<Product> productsList = ExampleData.getProducts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Done temporarily since not all users have icons, and not all products have images
        arrayList.add(productsList.get(1));
        arrayList.add(productsList.get(2));
        arrayList.add(productsList.get(3));
        arrayList.add(productsList.get(4));
        arrayList.add(productsList.get(5));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FeedRecyclerAdapter feedRecyclerAdapter = new FeedRecyclerAdapter(arrayList);
        recyclerView.setAdapter(feedRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void changeBookmark(View view) {
        ImageView bookmark = findViewById(R.id.bookmarkButton);
        bookmark.setTag(0);
        bookmark.setOnClickListener(v -> {
            if(bookmark.getTag().equals(0)) {
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                bookmark.setTag(1);
            } else {
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                bookmark.setTag(0);
            }
        });
    }
}