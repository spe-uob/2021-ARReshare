package com.example.ar_reshare;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModifyProduct extends AppCompatActivity {

    private ArrayList<Uri> uploadedImages = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");
        String productDescription = intent.getStringExtra("productDescription");
        Integer categoryID = intent.getIntExtra("categoryID",1);
        String condition = intent.getStringExtra("condition");
        String postcode = intent.getStringExtra("postcode");

        displayStringText(productName,productDescription,postcode);
        tickButton();
        uploadedImageView();
        categoryDropdown(categoryID);
        conditionDropdown(condition);
        returnListener();
    }

    // used a recycler view to display the images chosen by users
    private void uploadedImageView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        RecyclerView uploaded_image_list = findViewById(R.id.uploaded_image_list);
        uploaded_image_list.setLayoutManager(layoutManager);
        UploadImageAdapter adapter = new UploadImageAdapter(uploadedImages);
        uploaded_image_list.setAdapter(adapter);

    }

    //display product's name, description and postcode
    private void displayStringText(String name, String description, String postcode){
        EditText productNameText = (EditText)findViewById(R.id.add_product_name);
        productNameText.setText(name);
        EditText productDescriptionText = findViewById(R.id.add_product_description);
        productDescriptionText.setText(description);
        EditText productPostcodeText = findViewById(R.id.add_product_postcode);
        productPostcodeText.setText(postcode);
    }

    private void categoryDropdown(Integer categoryID){
        Spinner spinner = findViewById(R.id.category_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(categoryID - 1);
    }

    private void conditionDropdown(String condition){
        Spinner spinner = findViewById(R.id.condition_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.condition, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(condition);
        spinner.setSelection(spinnerPosition);
    }

    private void tickButton(){
        ImageView tick = findViewById(R.id.tick);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // implement a top left return arrow that returns to previous page when clicked
    private void returnListener(){

        ImageView returnArrow = findViewById(R.id.returnArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void confirmListener(){
        ImageView confirmCheck = findViewById(R.id.tick);
        confirmCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
