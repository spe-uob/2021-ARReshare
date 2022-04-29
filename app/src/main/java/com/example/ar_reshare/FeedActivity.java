package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FeedActivity extends Fragment {

    // Lists to initialise products
    List<Product> allProducts;
    List<Product> currentProductList;

    // Global Recycler View
    RecyclerView recyclerView;


    // CountDownLatch to ensure thread only works after results have been received from backend
    private CountDownLatch readyLatch;

    private final int TIMEOUT_IN_SECONDS = 10;

    FusedLocationProviderClient fusedLocationClient;

    // Location related attributes
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;
    // Built-in class which provides current location
    private Location userLocation;

    // Reference to adapter
    FeedRecyclerAdapter feedRecyclerAdapter;
    View view;

    // Distance Filtering
    private final int MIN_DISTANCE = 500; // metres
    private final int MAX_DISTANCE = 5500; //metres
    private final int DISTANCE_UNIT = 50; //metres
    private int maxDistanceRange = MAX_DISTANCE;
    private int tempDistanceRange = maxDistanceRange;

    // Category Filtering
    private final int UNCHECKED_CHIP_COLOUR = Color.parseColor("#dbdbdb");
    private Set<Category> categoriesSelected = new HashSet<>(Category.getCategories());
    private Set<Category> tempCategories = new HashSet<>(categoriesSelected);

    private final int DEFAULT_DARK_FONT = Color.parseColor("#363636");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_feed, container, false);
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        readyLatch = new CountDownLatch(1);
        currentProductList = new ArrayList<>();
        adapterCreator();
        BackendController.searchListings(0, 100, (success, searchResults) -> {
            if (success) {
                currentProductList.addAll(searchResults);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
            else {
                System.out.println("searchListings callback failed");
            }
        });

        // Link to Add Product page
        ImageView addProductButton = view.findViewById(R.id.feedAddProduct);
        addProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProduct.class);
            startActivity(intent);
        });

        // Create rotating animation for refresh button and scroll to top on refresh
        Animation spinningAnim = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        spinningAnim.setRepeatCount(0);
        spinningAnim.setDuration(500);
        ImageView refreshButton = view.findViewById(R.id.feedRefreshButton);
        refreshButton.setOnClickListener(v -> {
            refreshButton.startAnimation(spinningAnim);
            productListRecall();
            recyclerView.scrollToPosition(0);
        });

        // Filter according to user preferences
        ImageView filterButton = view.findViewById(R.id.feedFilterButton);
        setupFilterWindow(filterButton);
        waitOnAdapterCreation();
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_feed);

    }

    public void adapterCreator() {
        // Allows different products to be displayed as individual cards
        recyclerView = view.findViewById(R.id.recyclerView);
        feedRecyclerAdapter = new FeedRecyclerAdapter(currentProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(feedRecyclerAdapter);
        readyLatch.countDown();
    }

    private void waitOnAdapterCreation() {
        // Create a new thread to wait for the conditions
        new Thread(() -> {
            try {
                boolean success = readyLatch.await(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                if (success) {
                    // Any UI changes must be run on the UI Thread
                    getActivity().runOnUiThread(() -> {
                        getDeviceLocation();
                    });
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(),
                            "Failed to fetch your location or the products from the server. " +
                                    "Please ensure you have access to an internet connection.",
                            Toast.LENGTH_LONG).show());
                }
            } catch (InterruptedException e) {
                System.out.println("CRASH");
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Get the most recent location of the device
    private void getDeviceLocation() {
        SwipeActivity parent = (SwipeActivity) getActivity();
        try {
            if (parent.locationPermissionGranted) {
                parent.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                userLocation = location;
                                feedRecyclerAdapter.updateDistances(location);
                            }
                        });
            }
        } catch (SecurityException e)  {
            // Appropriate error catching
            System.out.println("Encountered" + e);
        }
    }

    // Filters page according to selected distance and categories
    private void productListRecall() {
        BackendController.searchListings(0, 100, (success, searchResults) -> {
            if (success) {
                allProducts = searchResults;
                getActivity().runOnUiThread(() -> constructNewPage(allProducts));
            }
            else {
                System.out.println("searchListings filter callback failed");
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void constructNewPage(List<Product> allProducts){
        List<Product> filteredList = allProducts.stream().filter(x -> {
            LatLng coordinates = x.getCoordinates();
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(coordinates.latitude);
            productLocation.setLongitude(coordinates.longitude);
            float dist = userLocation.distanceTo(productLocation);
            Category productCategory = Category.getCategoryById(x.getCategoryID());
            return dist <= maxDistanceRange && categoriesSelected.contains(productCategory);
        }).collect(Collectors.toList());
        currentProductList.clear();
        currentProductList.addAll(filteredList);
        feedRecyclerAdapter.notifyDataSetChanged();
    }

    // Setup filter results window
    private void setupFilterWindow(ImageView filterButton) {
        filterButton.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View filterWindow = inflater.inflate(R.layout.filter_popup, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            final PopupWindow popupWindow = new PopupWindow(filterWindow, width, height, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            popupWindow.setOnDismissListener(() -> cancelFilter(popupWindow));

            Button cancelFilterButton = filterWindow.findViewById(R.id.filterCancel);
            cancelFilterButton.setOnClickListener(cancel -> cancelFilter(popupWindow));

            Button confirmFilterButton = filterWindow.findViewById(R.id.filterConfirm);
            confirmFilterButton.setOnClickListener(confirm -> confirmFilter(popupWindow));

            setupCategoryChipGroup(filterWindow);
            setupDistanceSeekbar(filterWindow);
        });
    }

    // Confirm filter changes
    private void confirmFilter(PopupWindow popupWindow) {
        maxDistanceRange = tempDistanceRange;
        categoriesSelected = tempCategories;
        tempCategories = new HashSet<>(categoriesSelected);
        popupWindow.dismiss();
        productListRecall();
    }

    // Cancel filter changes
    private void cancelFilter(PopupWindow popupWindow) {
        tempDistanceRange = maxDistanceRange;
        tempCategories = new HashSet<>(categoriesSelected);
        popupWindow.dismiss();
    }

    // Setup category filtering UI
    private void setupCategoryChipGroup(View filterWindow) {
        ChipGroup filterCategories = filterWindow.findViewById(R.id.filterCategoryChipGroup);

        List<Category> categories = Category.getCategories();
        for (Category category : categories) {
            Chip categoryChip = new Chip(getActivity());
            categoryChip.setTag(category);
            categoryChip.setCheckable(true);
            if (categoriesSelected.contains(category)) {
                categoryChip.setChecked(true);
                int colour = category.getCategoryColour();
                if (isColourTooDark(colour)) {
                    categoryChip.setTextColor(Color.WHITE);
                }
                categoryChip.setChipBackgroundColor(ColorStateList.valueOf(colour));
            } else {
                categoryChip.setChipBackgroundColor(ColorStateList.valueOf(UNCHECKED_CHIP_COLOUR));
                categoryChip.setChecked(false);
            }
            categoryChip.setOnClickListener(v -> {
                Chip chip = (Chip) v;
                // Already been checked
                if (!chip.isChecked()) {
                    chip.setChecked(false);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(UNCHECKED_CHIP_COLOUR));
                    chip.setTextColor(DEFAULT_DARK_FONT);
                    //noinspection SuspiciousMethodCalls
                    tempCategories.remove(chip.getTag());
                    // Not checked
                } else {
                    Category category1 = (Category) chip.getTag();
                    chip.setChecked(true);
                    int colour = category1.getCategoryColour();
                    categoryChip.setChipBackgroundColor(ColorStateList.valueOf(colour));
                    if (isColourTooDark(colour)) {
                        categoryChip.setTextColor(Color.WHITE);
                    }
                    tempCategories.add(category1);
                }
            });
            categoryChip.setText(category.toString());
            categoryChip.setTextSize(14);
            filterCategories.addView(categoryChip);
        }
    }

    // Setup distance filtering UI
    private void setupDistanceSeekbar(View filterWindow) {
        SeekBar distanceBar = filterWindow.findViewById(R.id.distanceBar);
        TextView distanceAway = filterWindow.findViewById(R.id.distanceText);
        distanceBar.setProgress((maxDistanceRange - MIN_DISTANCE)/DISTANCE_UNIT);
        distanceAway.setText(MessageFormat.format("{0} metres away", maxDistanceRange));
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int distance = MIN_DISTANCE + progress*DISTANCE_UNIT;
                distanceAway.setText(MessageFormat.format("{0} metres away", distance));
                tempDistanceRange = distance;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Checks if colour contrast is high enough
    // If false is returned use a light font colour instead of dark
    private boolean isColourTooDark(int colour) {
        float backgroundLuminance = Color.luminance(colour);
        float textLuminance = Color.luminance(DEFAULT_DARK_FONT);
        float ratio = (float) ((backgroundLuminance + 0.05)/(textLuminance + 0.05));
        // Colour constants for accessibility needs - changing font colour based on contrast
        float MIN_CONTRAST_RATIO = 4.5f;
        return ratio < MIN_CONTRAST_RATIO;
    }




}