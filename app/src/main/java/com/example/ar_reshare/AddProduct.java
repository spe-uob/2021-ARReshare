package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddProduct extends AppCompatActivity {

    private ArrayList<Integer> uploadedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        uploadedImages.add(R.drawable.cup);
        uploadedImages.add(R.drawable.cup2);
        uploadedImages.add(R.drawable.pen);
        uploadedImages.add(R.drawable.upload);

        uploadedImageView();
        categoryDropdown();

    }
    private void uploadedImageView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        RecyclerView uploaded_image_list = findViewById(R.id.uploaded_image_list);
        uploaded_image_list.setLayoutManager(layoutManager);
        UploadImageAdapter adapter = new UploadImageAdapter(uploadedImages);
        uploaded_image_list.setAdapter(adapter);
    }
    private void categoryDropdown(){
        Spinner spinner = findViewById(R.id.category_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}