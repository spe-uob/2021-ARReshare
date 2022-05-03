package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;

public class ProfileActivity extends Fragment {

    private Integer contributorID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
//        contributorID = getArguments().getInt("contributorID", BackendController.loggedInUserID);
        contributorID = BackendController.loggedInUserID;

        System.out.println("The current userID is :" + contributorID);
        if (contributorID == BackendController.loggedInUserID) {
            System.out.println("current");
            getCurrentUserProfile(BackendController.loggedInUserID, view);
        } else {
            System.out.println("other");
            getOtherUserProfile(contributorID, view);
        }

        ImageButton settingButton = (ImageButton) view.findViewById(R.id.settingsButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        ImageButton backButton = (ImageButton) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        return view;
    }

    private void getCurrentUserProfile(int userID, View view) {
        ImageView profileIcon = view.findViewById(R.id.userProfileIcon);
        TextView contributorName = view.findViewById(R.id.userContributorName);
        BackendController.getProfileByID(0, 1, userID, (success, userProfile) -> {
            if (success) {
                getActivity().runOnUiThread(() -> {
                    if (userProfile.getProfilePic() == null) {
                        profileIcon.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        profileIcon.setImageBitmap(userProfile.getProfilePic());
                    }
                    contributorName.setText(userProfile.getName());
                });
                searchAccountListings(view);
                searchSavedListings(view);
            } else {
                System.out.println("getProfileByID callback failed");
            }
        });
    }

    private void getOtherUserProfile(int userID, View view) {
        ImageButton settingsButton = view.findViewById(R.id.settingsButton);
        settingsButton.setVisibility(View.GONE);

        TextView savedTitle = view.findViewById(R.id.savedText);
        savedTitle.setVisibility(View.GONE);
        View savedProducts = view.findViewById(R.id.savedProducts);
        savedProducts.setVisibility(View.GONE);

        ImageView profileIcon = view.findViewById(R.id.userProfileIcon);
        TextView contributorName = view.findViewById(R.id.userContributorName);

        BackendController.getProfileByID(0, 1, userID, (success, userProfile) -> {
            if (success) {
                getActivity().runOnUiThread(() -> {
                    if (userProfile.getProfilePic() == null) {
                        profileIcon.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        profileIcon.setImageBitmap(userProfile.getProfilePic());
                    }
                    contributorName.setText(userProfile.getName());
                });
                searchAccountListings(view);
            } else {
                System.out.println("getProfileByID callback failed");
            }
        });
    }

    public View.OnClickListener clickListener(Product product, View view) {
        return v -> {
            Fragment productActivity = new Fragment();
            Bundle bundle = new Bundle();
            bundle.putInt("productID", product.getId());
            bundle.putString("productName", product.getName());
            bundle.putString("productDescription", product.getDescription());
            bundle.putInt("contributorID", product.getContributorID());
            bundle.putDouble("lat", product.getCoordinates().latitude);
            bundle.putDouble("lng", product.getCoordinates().longitude);
            bundle.putString("postcode", product.getPostcode());
            productActivity.setArguments(bundle);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper, productActivity).addToBackStack(null).commit();
        };
    }

    public void searchAccountListings(View view) {
        TextView sharedTitle = view.findViewById(R.id.sharedText);
        View sharedProducts = view.findViewById(R.id.sharedProducts);
        TextView sharedStatus = view.findViewById(R.id.sharedStatus);

        ImageView shared1 = view.findViewById(R.id.shared1);
        ImageView shared2 = view.findViewById(R.id.shared2);
        ImageView shared3 = view.findViewById(R.id.shared3);

        BackendController.searchAccountListings(0, 3, contributorID, (success, ListingSearchResult) -> {
            if (success) {
                getActivity().runOnUiThread(() -> {
                    if (ListingSearchResult.isEmpty()) {
                        sharedTitle.setVisibility(View.GONE);
                        sharedProducts.setVisibility(View.GONE);
                        sharedStatus.setText("No products shared.");
                    } else if (ListingSearchResult.size() == 3) {
                        shared1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        shared2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(1), view));
                        shared3.setImageBitmap(ListingSearchResult.get(2).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(2), view));
                    } else if (ListingSearchResult.size() == 2) {
                        shared1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        shared2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(1), view));
                        shared3.setVisibility(View.GONE);
                    } else if (ListingSearchResult.size() == 1) {
                        shared1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        shared2.setVisibility(View.GONE);
                        shared3.setVisibility(View.GONE);
                    }
                    sharedStatus.setText(MessageFormat.format("{0} products shared.", ListingSearchResult.size()));
                });
            } else {
                System.out.println("searchAccountListings callback failed");
            }
        });
    }

    public void searchSavedListings(View view) {
        TextView savedTitle = view.findViewById(R.id.savedText);
        View savedProducts = view.findViewById(R.id.savedProducts);
        TextView savedStatus = view.findViewById(R.id.savedStatus);

        ImageView saved1 = view.findViewById(R.id.saved1);
        ImageView saved2 = view.findViewById(R.id.saved2);
        ImageView saved3 = view.findViewById(R.id.saved3);

        BackendController.searchSavedListings(0, 3, (success, ListingSearchResult) -> {
            if (success) {
                getActivity().runOnUiThread(() -> {
                    if (ListingSearchResult.isEmpty()) {
                        savedTitle.setVisibility(View.GONE);
                        savedProducts.setVisibility(View.GONE);
                        savedStatus.setText("No products saved.");
                    } else if (ListingSearchResult.size() == 3) {
                        saved1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        saved2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(1), view));
                        saved3.setImageBitmap(ListingSearchResult.get(2).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(2), view));
                    } else if (ListingSearchResult.size() == 2) {
                        saved1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        saved2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(1), view));
                        saved3.setVisibility(View.GONE);
                    } else if (ListingSearchResult.size() == 1) {
                        saved1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view));
                        saved2.setVisibility(View.GONE);
                        saved3.setVisibility(View.GONE);
                    }
                    savedStatus.setText(MessageFormat.format("{0} products saved.", ListingSearchResult.size()));
                });
            } else {
                System.out.println("searchSavedListings callback failed");
            }
        });
    }
}