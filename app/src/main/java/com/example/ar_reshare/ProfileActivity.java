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

        System.out.println("The current userID is :" + userID);
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

    private void getProfileById(int userID) {
        BackendController.getProfileByID(0, 3, userID, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if (success) {
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

                } else {
                    System.out.println("Failed to get profile");
                }
            }
        });
    }

    //productImage.setImageResource(currentProduct.getImages().get(0));

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
        BackendController.searchAccountListings(0, 3, 1, new BackendController.BackendSearchResultCallback() {
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
                                        intent.putExtra("contributorID", currentProduct1.getContributorID());
                                        intent.putExtra("productID", currentProduct1.getId());
                                        intent.putExtra("lat", currentProduct1.getCoordinates().latitude);
                                        intent.putExtra("lat", currentProduct1.getCoordinates().longitude);
                                        intent.putExtra("categoryID", currentProduct1.getCategoryID());
                                        intent.putExtra("postcode", currentProduct1.getPostcode());
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
                                        intent.putExtra("contributorID", currentProduct2.getContributorID());
                                        intent.putExtra("productID", currentProduct2.getId());
                                        intent.putExtra("lat", currentProduct2.getCoordinates().latitude);
                                        intent.putExtra("lat", currentProduct2.getCoordinates().longitude);
                                        intent.putExtra("categoryID", currentProduct2.getCategoryID());
                                        intent.putExtra("postcode", currentProduct2.getPostcode());
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
                                        intent.putExtra("contributorID", currentProduct3.getContributorID());
                                        intent.putExtra("productID", currentProduct3.getId());
                                        intent.putExtra("lat", currentProduct3.getCoordinates().latitude);
                                        intent.putExtra("lat", currentProduct3.getCoordinates().longitude);
                                        intent.putExtra("categoryID", currentProduct3.getCategoryID());
                                        intent.putExtra("postcode", currentProduct3.getPostcode());
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });

                } else{
                    System.out.println("Failed to get Account Listing");
                }
            }});
        };

    public void searchSavedListings(){
        BackendController.searchSavedListings(0, 3, 1, 1, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> savedListingSearchResult) {
                    if(success){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(savedListingSearchResult.size() >= 1) {
                                    savedProduct1 = savedListingSearchResult.get(0);
                                    ImageButton productImage1 = findViewById(R.id.saved1);

                                    productImage1.setImageBitmap(savedProduct1.getMainPic());

                                    productImage1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                                            intent.putExtra("product", savedProduct1);
                                            intent.putExtra("contributorID", savedProduct1.getContributorID());
                                            intent.putExtra("productID", savedProduct1.getId());
                                            intent.putExtra("lat", savedProduct1.getCoordinates().latitude);
                                            intent.putExtra("lat", savedProduct1.getCoordinates().longitude);
                                            intent.putExtra("categoryID", savedProduct1.getCategoryID());
                                            intent.putExtra("postcode", savedProduct1.getPostcode());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                if(savedListingSearchResult.size() >= 2) {
                                    savedProduct1 = savedListingSearchResult.get(1);
                                    ImageButton productImage1 = findViewById(R.id.saved2);

                                    productImage1.setImageBitmap(savedProduct2.getMainPic());

                                    productImage1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                                            intent.putExtra("product", savedProduct2);
                                            intent.putExtra("contributorID", savedProduct2.getContributorID());
                                            intent.putExtra("productID", savedProduct2.getId());
                                            intent.putExtra("lat", savedProduct2.getCoordinates().latitude);
                                            intent.putExtra("lat", savedProduct2.getCoordinates().longitude);
                                            intent.putExtra("categoryID", savedProduct2.getCategoryID());
                                            intent.putExtra("postcode", savedProduct2.getPostcode());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                if(savedListingSearchResult.size() >= 3) {
                                    savedProduct1 = savedListingSearchResult.get(2);
                                    ImageButton productImage1 = findViewById(R.id.saved3);

                                    productImage1.setImageBitmap(savedProduct3.getMainPic());

                                    productImage1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ProfileActivity.this, ProductPageActivity.class);
                                            intent.putExtra("product", savedProduct3);
                                            intent.putExtra("contributorID", savedProduct3.getContributorID());
                                            intent.putExtra("productID", savedProduct3.getId());
                                            intent.putExtra("lat", savedProduct3.getCoordinates().latitude);
                                            intent.putExtra("lat", savedProduct3.getCoordinates().longitude);
                                            intent.putExtra("categoryID", savedProduct3.getCategoryID());
                                            intent.putExtra("postcode", savedProduct3.getPostcode());
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });

                    } else{
                        System.out.println("Failed to get Saved Listing");
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