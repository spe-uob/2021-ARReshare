package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Fragment {
    public int profilePicId;
    private Product currentProduct1;
    private Product currentProduct2;
    private Product currentProduct3;
    private Product savedProduct1;
    private Product savedProduct2;
    private Product savedProduct3;
    private int userID;
    private int ReturnImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        Bundle bundle = this.getArguments();

        //Intent i = getActivity().getIntent();
        //userID = i.getIntExtra("userID", BackendController.loggedInUserID);

        System.out.println("The current userID is :" + userID);
        if (bundle == null) {
            System.out.println("current");
            getCurrentUserProfile(BackendController.loggedInUserID);
        } else {
            System.out.println("other");
            userID = bundle.getInt("userID");
            getOtherUserProfile(userID);
        }


        ImageButton settingButton = (ImageButton) view.findViewById(R.id.btS);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        ImageButton backButton = (ImageButton) view.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        return view;
    }

    private void getCurrentUserProfile(int userID) {
        BackendController.getProfileByID(0, 1, userID, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if (success) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView name = getActivity().findViewById(R.id.username);
                            name.setText(userProfile.getName());

                            ImageView profileIcon = getActivity().findViewById(R.id.avatar);
                            profileIcon.setImageBitmap(userProfile.getProfilePic());

                            searchAccountListing();
                            searchSavedListings();
                        }
                    });

                } else {
                    System.out.println("Failed to get profile");
                }
            }
        });
    }

    private void getOtherUserProfile(int userID){
        BackendController.getProfileByID(0, 1, userID, new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                if(success){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView name = getActivity().findViewById(R.id.username);
                            name.setText(userProfile.getName());

                            ImageView profileIcon = getActivity().findViewById(R.id.avatar);
                            profileIcon.setImageBitmap(userProfile.getProfilePic());

                            ImageButton settingsButton = (ImageButton) getActivity().findViewById(R.id.btS);

                            settingsButton.setVisibility(View.GONE);

                            //searchAccountListing();
                            searchOtherProfileListings(userProfile);

                            TextView savedTitle = getActivity().findViewById(R.id.savedTitle);
                            savedTitle.setVisibility(View.GONE);

                            ImageButton saved1 = getActivity().findViewById(R.id.saved1);
                            saved1.setVisibility(View.GONE);

                            ImageButton saved2 = getActivity().findViewById(R.id.saved2);
                            saved2.setVisibility(View.GONE);

                            ImageButton saved3 = getActivity().findViewById(R.id.saved3);
                            saved3.setVisibility(View.GONE);

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (ListingSearchResult.size() < 1){
                                ImageButton shared1 = getActivity().findViewById(R.id.shared1);
                                shared1.setVisibility(View.INVISIBLE);
                            } else {
                                TextView sharedtips = (TextView) getActivity().findViewById(R.id.sharedtips);
                                sharedtips.setVisibility(View.INVISIBLE);
                            }

                            if(ListingSearchResult.size() >= 1) {
                                currentProduct1 = ListingSearchResult.get(0);
                                ImageButton productImage1 = getActivity().findViewById(R.id.shared1);

                                productImage1.setImageBitmap(currentProduct1.getMainPic());

                                productImage1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", currentProduct1.getContributorID());
                                        bundle.putString("productName", currentProduct1.getName());
                                        bundle.putString("productDescription",currentProduct1.getDescription());
                                        bundle.putInt("productID", currentProduct1.getId());
                                        bundle.putDouble("lat", currentProduct1.getCoordinates().latitude);
                                        bundle.putDouble("lng", currentProduct1.getCoordinates().longitude);
                                        bundle.putString("postcode", currentProduct1.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }

                            if (ListingSearchResult.size() < 2){
                                ImageButton shared2 = getActivity().findViewById(R.id.shared2);
                                shared2.setVisibility(View.INVISIBLE);
                            }

                            if(ListingSearchResult.size() >= 2) {
                                currentProduct2 = ListingSearchResult.get(1);
                                ImageButton productImage2 = getActivity().findViewById(R.id.shared2);

                                productImage2.setImageBitmap(currentProduct2.getMainPic());


                                productImage2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", currentProduct2.getContributorID());
                                        bundle.putString("productName", currentProduct2.getName());
                                        bundle.putString("productDescription",currentProduct2.getDescription());
                                        bundle.putInt("productID", currentProduct2.getId());
                                        bundle.putDouble("lat",  currentProduct2.getCoordinates().latitude);
                                        bundle.putDouble("lng", currentProduct2.getCoordinates().longitude);
                                        bundle.putString("postcode", currentProduct2.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }

                            if (ListingSearchResult.size() < 3){
                                ImageButton shared3 = getActivity().findViewById(R.id.shared3);
                                shared3.setVisibility(View.INVISIBLE);
                            }

                            if(ListingSearchResult.size() >= 3) {
                                currentProduct3 = ListingSearchResult.get(2);
                                ImageButton productImage3 = getActivity().findViewById(R.id.shared3);

                                productImage3.setImageBitmap(currentProduct3.getMainPic());

                                productImage3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", currentProduct3.getContributorID());
                                        bundle.putString("productName", currentProduct3.getName());
                                        bundle.putString("productDescription",currentProduct3.getDescription());
                                        bundle.putInt("productID", currentProduct3.getId());
                                        bundle.putDouble("lat",  currentProduct3.getCoordinates().latitude);
                                        bundle.putDouble("lng", currentProduct3.getCoordinates().longitude);
                                        bundle.putString("postcode", currentProduct3.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }
                        }
                    });

                } else{
                    System.out.println("Failed to get Account Listing");
                }
            }});
    }

    public void searchOtherProfileListings(User user){
        List<Product> userProduct = user.getListings();

        BackendController.initialiseProducts(userProduct, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> searchResults) {
                getActivity().runOnUiThread(() -> {

                    if(searchResults.size() < 1){
                        ImageButton shared1 = (ImageButton) getActivity().findViewById(R.id.shared1);
                        shared1.setVisibility(View.GONE);
                    } else {
                        TextView savedtips = (TextView) getActivity().findViewById(R.id.savedtips);
                        savedtips.setVisibility(View.INVISIBLE);
                    }

                    if(searchResults.size() >= 1){
                        ImageButton shared1 = (ImageButton) getActivity().findViewById(R.id.shared1);
                        sharedProductShow(shared1, searchResults.get(0));
                    }

                    if(searchResults.size() < 2){
                        ImageButton shared2 = (ImageButton) getActivity().findViewById(R.id.shared2);
                        shared2.setVisibility(View.INVISIBLE);
                    }

                    if (searchResults.size() >= 2){
                        ImageButton shared2 = (ImageButton) getActivity().findViewById(R.id.shared2);
                        sharedProductShow(shared2, searchResults.get(1));
                    }

                    if(searchResults.size() < 3){
                        ImageButton shared3 = (ImageButton) getActivity().findViewById(R.id.shared3);
                        shared3.setVisibility(View.INVISIBLE);
                    }

                    if (searchResults.size() >= 3){
                        ImageButton shared3 = (ImageButton) getActivity().findViewById(R.id.shared3);
                        sharedProductShow(shared3, searchResults.get(2));
                    }
                });
            }
        });
    }


    private void sharedProductShow(ImageButton shared, Product product){

        shared.setImageBitmap(product.getMainPic());

        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("contributorID", product.getContributorID());
                bundle.putString("productName", product.getName());
                bundle.putString("productDescription", product.getDescription());
                bundle.putInt("productID", product.getId());
                bundle.putDouble("lat",  product.getCoordinates().latitude);
                bundle.putDouble("lng", product.getCoordinates().longitude);
                bundle.putString("postcode", product.getPostcode());
                ProductPageActivity productFragment = new ProductPageActivity();
                productFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
            }
        });
    }


    public void searchSavedListings() {
        BackendController.searchSavedListings(0, 3, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> savedListingSearchResult) {
                if (success) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (savedListingSearchResult.size() < 1) {
                                ImageButton savedImage1 = getActivity().findViewById(R.id.saved1);
                                savedImage1.setVisibility(View.INVISIBLE);
                            } else {
                                TextView savedtips = (TextView) getActivity().findViewById(R.id.savedtips);
                                savedtips.setVisibility(View.INVISIBLE);
                            }

                            if (savedListingSearchResult.size() >= 1) {
                                savedProduct1 = savedListingSearchResult.get(0);
                                ImageButton productImage1 = getActivity().findViewById(R.id.saved1);

                                productImage1.setImageBitmap(savedProduct1.getMainPic());

                                productImage1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", savedProduct1.getContributorID());
                                        bundle.putString("productName", savedProduct1.getName());
                                        bundle.putString("productDescription",savedProduct1.getDescription());
                                        bundle.putInt("productID", savedProduct1.getId());
                                        bundle.putDouble("lat", savedProduct1.getCoordinates().latitude);
                                        bundle.putDouble("lng", savedProduct1.getCoordinates().longitude);
                                        bundle.putString("postcode", savedProduct1.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }

                            if (savedListingSearchResult.size() < 2) {
                                ImageButton savedImage2 = getActivity().findViewById(R.id.saved2);
                                savedImage2.setVisibility(View.INVISIBLE);
                            }

                            if (savedListingSearchResult.size() >= 2) {
                                savedProduct2 = savedListingSearchResult.get(1);
                                ImageButton productImage2 = getActivity().findViewById(R.id.saved2);

                                productImage2.setImageBitmap(savedProduct2.getMainPic());

                                productImage2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", savedProduct2.getContributorID());
                                        bundle.putString("productName", savedProduct2.getName());
                                        bundle.putString("productDescription",savedProduct2.getDescription());
                                        bundle.putInt("productID", savedProduct2.getId());
                                        bundle.putDouble("lat", savedProduct2.getCoordinates().latitude);
                                        bundle.putDouble("lng", savedProduct2.getCoordinates().longitude);
                                        bundle.putString("postcode", savedProduct2.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }

                            if (savedListingSearchResult.size() < 3) {
                                ImageButton savedImage3 = getActivity().findViewById(R.id.saved3);
                                savedImage3.setVisibility(View.INVISIBLE);
                            }

                            if (savedListingSearchResult.size() >= 3) {
                                savedProduct3 = savedListingSearchResult.get(2);
                                ImageButton productImage3 = getActivity().findViewById(R.id.saved3);

                                productImage3.setImageBitmap(savedProduct3.getMainPic());

                                productImage3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("contributorID", savedProduct3.getContributorID());
                                        bundle.putString("productName", savedProduct3.getName());
                                        bundle.putString("productDescription",savedProduct3.getDescription());
                                        bundle.putInt("productID", savedProduct3.getId());
                                        bundle.putDouble("lat", savedProduct3.getCoordinates().latitude);
                                        bundle.putDouble("lng", savedProduct3.getCoordinates().longitude);
                                        bundle.putString("postcode", savedProduct3.getPostcode());
                                        ProductPageActivity productFragment = new ProductPageActivity();
                                        productFragment.setArguments(bundle);
                                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                                        activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
                                    }
                                });
                            }
                        }
                    });

                } else {
                    System.out.println("Failed to get Saved Listing");
                }
            }
        });
    }


//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
}