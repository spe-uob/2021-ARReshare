package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private Button MessageButton;
    private ImageButton BackButton;
    private ImageButton ProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User A = ExampleData.getUsers().get(0);
        Product B = ExampleData.getProducts().get(0);

        TextView name = findViewById(R.id.username);
        name.setText(A.getName());

        TextView bio = findViewById(R.id.description);
        bio.setText(A.getBio());

        ImageView avator1 = findViewById(R.id.avator);
        avator1.setImageResource(A.getProfileIcon());

        ImageButton product1 = findViewById(R.id.shared1);
        product1.setImageResource(B.getImages().get(0));

        MessageButton = (Button)findViewById(R.id.btM);
        MessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MessagingActivity.class);
            }
        });

        BackButton = (ImageButton)findViewById(R.id.back);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ProductButton = (ImageButton) findViewById(R.id.shared1);
        ProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                intent.putExtra("name", A.getName());
                intent.putExtra("product", B);
                intent.putExtra("contributor", B.getContributor());
                intent.putExtra("profilePicId", B.getImages().get(0));
                intent.putIntegerArrayListExtra("productPicId", (ArrayList<Integer>) B.getImages());

                startActivity(intent);
            }
        });
    }
}