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
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private Button MessageButton;
    private ImageButton BackButton;
    private ImageButton ProductButton;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i= getIntent();
        User currentUser = i.getParcelableExtra("user");

        User A = currentUser;
        List<Product> products = ExampleData.getProducts();

        TextView name = findViewById(R.id.username);
        name.setText(currentUser.getName());

        TextView bio = findViewById(R.id.description);
        bio.setText(currentUser.getBio());

        ImageView avator1 = findViewById(R.id.avator);
        avator1.setImageResource(currentUser.getProfileIcon());

        ImageButton product1 = findViewById(R.id.shared1);
        for (Product product : products) {
            if(product.getContributor().equals(currentUser)){
                currentProduct = product;
            }
        }
        product1.setImageResource(currentProduct.getImages().get(0));

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
                intent.putExtra("product", currentProduct);
                intent.putExtra("contributor", currentProduct.getContributor());
                intent.putExtra("profilePicId", currentProduct.getContributor().getProfileIcon());
                intent.putIntegerArrayListExtra("productPicId", (ArrayList<Integer>) currentProduct.getImages());

                startActivity(intent);
            }
        });
    }
}