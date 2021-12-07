package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ArrayList<Product> arrayList = new ArrayList<>();

        User Artur = new User("Artur","",1);
        Artur.setProfileIcon(R.drawable.artur_profile_icon);
        arrayList.add(new Product("Festive Coffee Cup", "Get your hands on this beautiful coffee cup now!",
                Artur, Category.OTHER, 51.45120306024447, -2.5869936269149303));
        arrayList.get(0).addImages(R.drawable.coffee_cup);

        User Arafat = new User("Arafat","",1);
        Arafat.setProfileIcon(R.drawable.arfi_profile_icon);
        arrayList.add(new Product("Magic Pen", "Take amazing notes with this stylish magic pen.",
                Arafat, Category.OTHER, 51.459040571152514, -2.6022736036387366));
        arrayList.get(1).addImages(R.drawable.pen);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
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