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
    private Button MessageButton;
    private ImageButton BackButton;
    private User contributor;
    private int profilePicId;
    private String bio;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        contributor = i.getParcelableExtra("contributor");
        if (contributor == null) {
            contributor = ExampleData.getUsers().get(0);
            profilePicId = contributor.getProfileIcon();
            bio = contributor.getBio();
            currentProduct = ExampleData.getProducts().get(0);
        } else {
            profilePicId = i.getIntExtra("profilePicId", 0);
            bio = i.getStringExtra("bio");
        }

        List<Product> products = ExampleData.getProducts();

        TextView name = findViewById(R.id.username);
        name.setText(contributor.getName());

        TextView bioText = findViewById(R.id.description);
        bioText.setText(bio);

        ImageView profileIcon = findViewById(R.id.avatar);
        profileIcon.setImageResource(profilePicId);

        ImageView productImage = findViewById(R.id.shared1);
        for (Product product : products) {
            if(product.getContributor().getName().equals(contributor.getName())){
                currentProduct = product;
            }
        }
        productImage.setImageResource(currentProduct.getImages().get(0));

        MessageButton = (Button)findViewById(R.id.btM);
        MessageButton.setOnClickListener(new View.OnClickListener() {
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

        BackButton = (ImageButton)findViewById(R.id.back);
        BackButton.setOnClickListener(new View.OnClickListener() {
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