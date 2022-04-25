package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kotlin.contracts.Returns;

public class ProfileActivity extends AppCompatActivity {
    public int profilePicId;
    private Product currentProduct1;
    private Product currentProduct2;
    private Product currentProduct3;
    private Product savedProduct1;
    private Product savedProduct2;
    private Product savedProduct3;
    private int userID;
    private int ReturnImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        userID = i.getIntExtra("userID", BackendController.loggedInUserID);

        System.out.println("The current userID is :" + userID );
        if (userID == BackendController.loggedInUserID) {
            System.out.println("111111111111");
            getProfileById(userID);
        } else {
            System.out.println("222222222222222");
            getProfileById2(userID);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView name = findViewById(R.id.username);
                            name.setText(userProfile.getName());

                            TextView bioText = findViewById(R.id.description);
                            bioText.setText(userProfile.getBio());

                            ImageView profileIcon = findViewById(R.id.avatar);
                            profileIcon.setImageBitmap(userProfile.getProfilePic());

                            Button settingsButton = (Button) findViewById(R.id.btS);
                            settingsButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                                    startActivity((intent));
                                }
                            });

                            Button messageButton1 = (Button) findViewById(R.id.btM);
                            messageButton1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, ChatListActivity.class);
                                    startActivity(intent);
                                }
                            });

                            searchAccountListing();
                        }
                    });

                } else{
                    System.out.println("Failed to get profile");
                }
            }
        });
    }

    private void getProfileById2(int userID){
        BackendController.getProfileByID(0, 1, userID, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if(success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView name = findViewById(R.id.username);
                            name.setText(userProfile.getName());

                            TextView bioText = findViewById(R.id.description);
                            bioText.setText(userProfile.getBio());

                            ImageView profileIcon = findViewById(R.id.avatar);
                            profileIcon.setImageBitmap(userProfile.getProfilePic());

                            Button settingsButton = (Button) findViewById(R.id.btS);
                            settingsButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                                    startActivity((intent));
                                }
                            });

                            Button messageButton1 = (Button) findViewById(R.id.btM);
                            messageButton1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, ChatListActivity.class);
                                    startActivity(intent);
                                }
                            });
                            searchAccountListing();
                        }
                    });


                } else{
                    System.out.println("Failed to get profile");
                }
            }
        });
    }

    public void searchAccountListing(){
        BackendController.searchAccountListings(0, 3, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> ListingSearchResult) {
                if(success){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ListingSearchResult.size() >= 1) {
                                currentProduct1 = ListingSearchResult.get(0);
                                ImageButton productImage1 = findViewById(R.id.shared1);

                                productImage1.setImageBitmap(currentProduct1.getMainPic());

                                productImage1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                                        intent.putExtra("product", currentProduct1);
                                        intent.putExtra("contributor", currentProduct1.getContributor());
                                        intent.putExtra("profilePicId", currentProduct1.getContributor().getProfilePic());
                                        intent.putExtra("productPicId", (ArrayList<Integer>) currentProduct1.getImages());
                                        startActivity(intent);
                                    }
                                });
                            }

                            if(ListingSearchResult.size() >= 2) {
                                currentProduct2 = ListingSearchResult.get(1);
                                ImageButton productImage2 = findViewById(R.id.shared2);

                                productImage2.setImageBitmap(currentProduct2.getMainPic());


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
                            }

                            if(ListingSearchResult.size() >= 3) {
                                currentProduct3 = ListingSearchResult.get(2);
                                ImageButton productImage3 = findViewById(R.id.shared3);

                                productImage3.setImageBitmap(currentProduct3.getMainPic());

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
                            }
                        }
                    });

                } else{
                    System.out.println("Failed to get Account Listing");
                }
            }
        });
    }

    public void searchSavedListings(){
        BackendController.searchSavedListings(0, 3, 1, 1, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> savedListingSearchResult) {
                    if(success){

                    } else{

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