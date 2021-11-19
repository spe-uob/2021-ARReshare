package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ModelClass> arrayList = new ArrayList<>();
        arrayList.add(new ModelClass(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
                "Festive Coffee Cup", "Get your hands on this beautiful coffee cup now!"));
        arrayList.add(new ModelClass(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
                "Magic Pen", "Get your hands on this stylish magic pen now!"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}