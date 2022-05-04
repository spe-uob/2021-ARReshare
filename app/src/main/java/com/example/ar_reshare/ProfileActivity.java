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
import java.util.List;

public class ProfileActivity extends Fragment {

    private Integer contributorID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        contributorID = getArguments().getInt("contributorID", BackendController.loggedInUserID);

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
                getActivity().getSupportFragmentManager().popBackStack();
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
        TextView savedStatus = view.findViewById(R.id.savedStatus);
        savedStatus.setVisibility(View.GONE);

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
                searchOtherProfileListings(view, userProfile);
            } else {
                System.out.println("getProfileByID callback failed");
            }
        });
    }

    public View.OnClickListener clickListener(Product product, View view, boolean isSaved) {
        return v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("contributorID",product.getContributorID());
            bundle.putString("productName",product.getName());
            bundle.putString("productDescription",product.getDescription());
            bundle.putInt("productID",product.getId());
            bundle.putDouble("lat", product.getCoordinates().latitude);
            bundle.putDouble("lng",product.getCoordinates().longitude);
            bundle.putString("postcode",product.getPostcode());
            bundle.putBoolean("isSaved", isSaved);
            ProductPageActivity productFragment = new ProductPageActivity();
            productFragment.setArguments(bundle);
            productFragment.setIsFromFeed(false);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
        };
    }

    private void searchOtherProfileListings(View view, User userProfile) {
        TextView sharedTitle = view.findViewById(R.id.sharedText);
        View sharedProducts = view.findViewById(R.id.sharedProducts);
        TextView sharedStatus = view.findViewById(R.id.sharedStatus);

        ImageView shared1 = view.findViewById(R.id.shared1);
        ImageView shared2 = view.findViewById(R.id.shared2);
        ImageView shared3 = view.findViewById(R.id.shared3);

        BackendController.initialiseProducts(userProfile.getListings(), new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> searchResults) {
                if (success) {
                    getActivity().runOnUiThread(() -> {
                        if (searchResults.isEmpty()) {
                            sharedTitle.setVisibility(View.GONE);
                            sharedProducts.setVisibility(View.GONE);
                            sharedStatus.setText("No products shared.");
                        } else if (searchResults.size() == 3) {
                            shared1.setImageBitmap(searchResults.get(0).getMainPic());
                            shared1.setOnClickListener(clickListener(searchResults.get(0), view, false));
                            shared2.setImageBitmap(searchResults.get(1).getMainPic());
                            shared2.setOnClickListener(clickListener(searchResults.get(1), view, false));
                            shared3.setImageBitmap(searchResults.get(2).getMainPic());
                            shared3.setOnClickListener(clickListener(searchResults.get(2), view, false));
                        } else if (searchResults.size() == 2) {
                            shared1.setImageBitmap(searchResults.get(0).getMainPic());
                            shared1.setOnClickListener(clickListener(searchResults.get(0), view, false));
                            shared2.setImageBitmap(searchResults.get(1).getMainPic());
                            shared2.setOnClickListener(clickListener(searchResults.get(1), view, false));
                            shared3.setVisibility(View.GONE);
                        } else if (searchResults.size() == 1) {
                            shared1.setImageBitmap(searchResults.get(0).getMainPic());
                            shared1.setOnClickListener(clickListener(searchResults.get(0), view, false));
                            shared2.setVisibility(View.GONE);
                            shared3.setVisibility(View.GONE);
                        }
                        sharedStatus.setText(MessageFormat.format("{0} product(s) shared.", searchResults.size()));
                    });
                } else {
                    System.out.println("searchAccountListings callback failed");
                }
            }
        });
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
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, false));
                        shared2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        shared2.setOnClickListener(clickListener(ListingSearchResult.get(1), view, false));
                        shared3.setImageBitmap(ListingSearchResult.get(2).getMainPic());
                        shared3.setOnClickListener(clickListener(ListingSearchResult.get(2), view, false));
                    } else if (ListingSearchResult.size() == 2) {
                        shared1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, false));
                        shared2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        shared2.setOnClickListener(clickListener(ListingSearchResult.get(1), view, false));
                        shared3.setVisibility(View.GONE);
                    } else if (ListingSearchResult.size() == 1) {
                        shared1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        shared1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, false));
                        shared2.setVisibility(View.GONE);
                        shared3.setVisibility(View.GONE);
                    }
                    sharedStatus.setText(MessageFormat.format("{0} product(s) shared.", ListingSearchResult.size()));
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
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, true));
                        saved2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        saved2.setOnClickListener(clickListener(ListingSearchResult.get(1), view, true));
                        saved3.setImageBitmap(ListingSearchResult.get(2).getMainPic());
                        saved3.setOnClickListener(clickListener(ListingSearchResult.get(2), view, true));
                    } else if (ListingSearchResult.size() == 2) {
                        saved1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, true));
                        saved2.setImageBitmap(ListingSearchResult.get(1).getMainPic());
                        saved2.setOnClickListener(clickListener(ListingSearchResult.get(1), view, true));
                        saved3.setVisibility(View.GONE);
                    } else if (ListingSearchResult.size() == 1) {
                        saved1.setImageBitmap(ListingSearchResult.get(0).getMainPic());
                        saved1.setOnClickListener(clickListener(ListingSearchResult.get(0), view, true));
                        saved2.setVisibility(View.GONE);
                        saved3.setVisibility(View.GONE);
                    }
                    savedStatus.setText(MessageFormat.format("{0} product(s) saved.", ListingSearchResult.size()));
                });
            } else {
                System.out.println("searchSavedListings callback failed");
            }
        });
    }
}