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
    private Product currentProduct1;
    private Product currentProduct2;
    private Product currentProduct3;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        userID = i.getIntExtra("userID", 0);

        if (userID == BackendController.loggedInUserID) {
            getProfileById(userID);
        } else {
            getProfileById(userID);
        }

        //productImage.setImageResource(currentProduct.getImages().get(0));

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void getProfileById(int userID){
        BackendController.getProfileByID(0, 3, userID, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if(success){

                    TextView name = findViewById(R.id.username);
                    name.setText(userProfile.getName());

                    TextView bioText = findViewById(R.id.description);
                    bioText.setText(userProfile.getBio());

                    ImageView profileIcon = findViewById(R.id.avatar);
                    profileIcon.setImageResource(userProfile.getProfileIcon());

                    currentProduct1 = userProfile.getListings().get(0);
                    List<Product> products = userProfile.getListings();

                    ImageButton productImage1 = findViewById(R.id.shared1);
                    for (Product product : products) {
                        if(product.getContributor().getName().equals(userProfile.getName())){
                            currentProduct1 = product;
                        }
                    }

                    productImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                            intent.putExtra("product", currentProduct1);
                            intent.putExtra("contributor", currentProduct1.getContributor());
                            intent.putExtra("profilePicId", currentProduct1.getContributor().getProfileIcon());
                            intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct1.getImages());
                            startActivity(intent);
                        }
                    });

                    currentProduct2 = userProfile.getListings().get(1);
                    ImageButton productImage2 = findViewById(R.id.shared2);
                    for (Product product : products) {
                        if(product.getContributor().getName().equals(userProfile.getName())){
                            currentProduct2 = product;
                        }
                    }

                    productImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                            intent.putExtra("product", currentProduct2);
                            intent.putExtra("contributor", currentProduct2.getContributor());
                            intent.putExtra("profilePicId", currentProduct2.getContributor().getProfileIcon());
                            intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct2.getImages());
                            startActivity(intent);
                        }
                    });

                    currentProduct3 = userProfile.getListings().get(2);
                    ImageButton productImage3 = findViewById(R.id.shared3);
                    for (Product product : products) {
                        if(product.getContributor().getName().equals(userProfile.getName())){
                            currentProduct3 = product;
                        }
                    }

                    productImage3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                            intent.putExtra("product", currentProduct3);
                            intent.putExtra("contributor", currentProduct3.getContributor());
                            intent.putExtra("profilePicId", currentProduct3.getContributor().getProfileIcon());
                            intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct3.getImages());
                            startActivity(intent);
                        }
                    });

                    Button messageButton1 = (Button) findViewById(R.id.btM);
                    messageButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ChatListActivity.class);
                            intent.putExtra("product", currentProduct1);
                            intent.putExtra("contributor", currentProduct1.getContributor());
                            intent.putExtra("profilePicId", currentProduct1.getContributor().getProfileIcon());
                            intent.putExtra("user", userProfile.getName());
                            v.getContext().startActivity(intent);
                        }
                    });
                } else{
                    System.out.println("Failed to get profile");
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}