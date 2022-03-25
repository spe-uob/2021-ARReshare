package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedActivity extends AppCompatActivity {

    // Array to initialise products
    List<Product> productList =
            ExampleData.getProducts().subList(1, ExampleData.getProducts().size());

    // Location related attributes
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;
    // Built-in class which provides current location
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    // Reference to adapter
    FeedRecyclerAdapter feedRecyclerAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Allows different products to be displayed as individual cards
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        feedRecyclerAdapter = new FeedRecyclerAdapter(productList);
        recyclerView.setAdapter(feedRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Request location permissions if needed and get latest location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        getDeviceLocation();

        // Link to Add Product page
        ImageView addProductButton = findViewById(R.id.feedAddProduct);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        // Create rotating animation for refresh button and scroll to top on refresh
        Animation spinningAnim = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        spinningAnim.setRepeatCount(0);
        spinningAnim.setDuration(500);
        ImageView refreshButton = findViewById(R.id.feedRefreshButton);
        refreshButton.setOnClickListener(v -> {
            refreshButton.startAnimation(spinningAnim);
            recyclerView.scrollToPosition(0);
        });

        // Filter according to user preferences
        ImageView filterButton = findViewById(R.id.feedFilterButton);
        setupFilterWindow(filterButton);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the grantResults array will be empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                locationPermissionGranted = true;
                System.out.println("Location has been granted");
            } else {
                // Explain to user that the feature is unavailable because
                // the permissions have not been granted
                System.out.println("Feature unavailable due to lack of permissions");
            }
        }
    }

    // Request location permissions from the device. We will receive a callback
    // to onRequestPermissionsResult with the results.
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Location permission has already been granted previously
            locationPermissionGranted = true;
        } else if (shouldShowRequestPermissionRationale("FINE_LOCATION")) {
            // Explain to the user why the location permission is needed
            System.out.println("Please enable location to use our app");
        } else {
            // If the location permission has not been granted already,
            // open a window requesting this permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Get the most recent location of the device
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
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
    @SuppressLint("NotifyDataSetChanged")
    private void filterPage() {
        List<Product> allProducts = ExampleData.getProducts()
                .subList(1, ExampleData.getProducts().size());
        List<Product> filteredList = allProducts.stream().filter(x -> {
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(x.getLocation().latitude);
            productLocation.setLongitude(x.getLocation().longitude);
            float dist = userLocation.distanceTo(productLocation);
            return dist <= maxDistanceRange && categoriesSelected.contains(x.getCategory());
        }).collect(Collectors.toList());
        productList.clear();
        productList.addAll(filteredList);
        feedRecyclerAdapter.notifyDataSetChanged();
    }

    // Setup filter results window
    private void setupFilterWindow(ImageView filterButton) {
        filterButton.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View filterWindow = inflater.inflate(R.layout.filter_popup, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            final PopupWindow popupWindow = new PopupWindow(filterWindow, width, height, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            popupWindow.setOnDismissListener(() -> cancelFilter(popupWindow));

            Button cancelFilterButton = filterWindow.findViewById(R.id.filterCancel);
            cancelFilterButton.setOnClickListener(v1 -> cancelFilter(popupWindow));

            Button confirmFilterButton = filterWindow.findViewById(R.id.filterConfirm);
            confirmFilterButton.setOnClickListener(v12 -> confirmFilter(popupWindow));

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
        filterPage();
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
            Chip categoryChip = new Chip(FeedActivity.this);
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