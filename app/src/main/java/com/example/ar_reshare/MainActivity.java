package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ModelClass> arrayList = new ArrayList<>();
        arrayList.add(new ModelClass(R.drawable.artur_profile_icon, "Artur", R.drawable.coffee_cup,
                "Festive Coffee Cup", "Get your hands on this beautiful coffee cup now!"));
        arrayList.add(new ModelClass(R.drawable.arfi_profile_icon, "Arafat", R.drawable.pen,
                "Magic Pen", "Take amazing notes with this stylish magic pen."));

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