package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    public int profilePicId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        User contributor = i.getParcelableExtra("contributor");
        String bio;
        if (contributor == null) {
            contributor = ExampleData.getUsers().get(0);
            profilePicId = contributor.getProfileIcon();
            currentProduct = ExampleData.getProducts().get(0);
        } else {
            profilePicId = i.getIntExtra("profilePicId", 0);
            contributor.setProfileIcon(profilePicId);
            bio = i.getStringExtra("bio");
            contributor.setBio(bio);
        }

        List<Product> products = ExampleData.getProducts();

        TextView name = findViewById(R.id.username);
        name.setText(contributor.getName());

        TextView bioText = findViewById(R.id.description);
        bioText.setText(contributor.getBio());

        ImageView profileIcon = findViewById(R.id.avatar);
        profileIcon.setImageResource(contributor.getProfileIcon());

        ImageButton productImage = findViewById(R.id.shared1);
        for (Product product : products) {
            if(product.getContributor().getName().equals(contributor.getName())){
                currentProduct = product;
            }
        }
        productImage.setImageResource(currentProduct.getImages().get(0));

        Button messageButton = (Button) findViewById(R.id.btM);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessagingActivity.class);
                intent.putExtra("product", currentProduct);
                intent.putExtra("contributor", currentProduct.getContributor());
                intent.putExtra("profilePicId", currentProduct.getContributor().getProfileIcon());
                intent.putExtra("user", ExampleData.getUsers().get(0));
                v.getContext().startActivity(intent);
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                intent.putExtra("product", currentProduct);
                intent.putExtra("contributor", currentProduct.getContributor());
                intent.putExtra("profilePicId", currentProduct.getContributor().getProfileIcon());
                intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct.getImages());
                startActivity(intent);
            }
        });
    }
}