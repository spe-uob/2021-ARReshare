package com.example.ar_reshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.ar_reshare.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener
        {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // Google Maps built-in class which provider current location
    private FusedLocationProviderClient fusedLocationClient;
    // The users last known location
    private Location lastKnownLocation;

    // Filtering Results

    // Distance Filtering
    private final int MIN_DISTANCE = 500; // metres
    private final int MAX_DISTANCE = 5500; //metres
    private final int DISTANCE_UNIT = 50; //metres
    private int maxDistanceRange = MAX_DISTANCE;
    private int tempDistanceRange = MIN_DISTANCE;

    // Category Filtering
    private final int UNCHECKED_CHIP_COLOUR = Color.parseColor("#dbdbdb");
    private Set<Category> categoriesSelected = new HashSet<>(Category.getCategories());
    private Set<Category> tempCategories = new HashSet<>(categoriesSelected);

    // Colour constants for accessibility needs - changing font colour based on contrast
    private final float MIN_CONTRAST_RATIO = 4.5f;
    private final int DEFAULT_DARK_FONT = Color.parseColor("#363636");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set the return arrow button on click event
        ImageButton returnArrow = findViewById(R.id.returnToMainArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        ImageButton filterButton = findViewById(R.id.filterMapButton);
        setupFilterWindow(filterButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
    }

    // Setup filter results window
    private void setupFilterWindow(ImageButton filterButton) {
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View filterWindow = inflater.inflate(R.layout.filter_popup, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                // Allows to tap outside the popup to dismiss it
                boolean focusable = true;

                final PopupWindow popupWindow = new PopupWindow(filterWindow, width, height, focusable);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        cancelFilter(popupWindow);
                    }
                });

                Button cancelFilterButton = filterWindow.findViewById(R.id.filterCancel);
                cancelFilterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelFilter(popupWindow);
                    }
                });

                Button confirmFilterButton = filterWindow.findViewById(R.id.filterConfirm);
                confirmFilterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmFilter(popupWindow);
                    }
                });

                setupCategoryChipGroup(filterWindow);
                setupDistanceSeekbar(filterWindow, popupWindow);
            }
        });
    }

    // Confirm filter changes
    private void confirmFilter(PopupWindow popupWindow) {
        maxDistanceRange = tempDistanceRange;
        categoriesSelected = tempCategories;
        tempCategories = new HashSet<>(categoriesSelected);
        popupWindow.dismiss();
        populateMap(mMap);
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
            Chip categoryChip = new Chip(MapsActivity.this);
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
            categoryChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Chip chip = (Chip) v;
                    // Already been checked
                    if (!chip.isChecked()) {
                        chip.setChecked(false);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(UNCHECKED_CHIP_COLOUR));
                        chip.setTextColor(DEFAULT_DARK_FONT);
                        tempCategories.remove((Category) chip.getTag());
                        // Not checked
                    } else {
                        Category category = (Category) chip.getTag();
                        chip.setChecked(true);
                        int colour = category.getCategoryColour();
                        categoryChip.setChipBackgroundColor(ColorStateList.valueOf(colour));
                        if (isColourTooDark(colour)) {
                            categoryChip.setTextColor(Color.WHITE);
                        }
                        tempCategories.add(category);
                    }
                }
            });
            categoryChip.setText(category.toString());
            categoryChip.setTextSize(14);
            filterCategories.addView(categoryChip);
        }
    }

    // Setup distance filtering UI
    private void setupDistanceSeekbar(View filterWindow, PopupWindow popupWindow) {
        SeekBar distanceBar = filterWindow.findViewById(R.id.distanceBar);
        TextView distanceAway = filterWindow.findViewById(R.id.distanceText);
        distanceBar.setProgress((maxDistanceRange - MIN_DISTANCE)/DISTANCE_UNIT);
        distanceAway.setText(maxDistanceRange + " metres away");
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int distance = MIN_DISTANCE + progress*DISTANCE_UNIT;
                distanceAway.setText(distance + " metres away");
                tempDistanceRange = distance;
                return;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                return;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                return;
            }
        });
    }

    // When leaving the Map Activity always animate sliding down
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    // When map is loaded, checks for location permissions and configures the initial
    // state of the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            getDeviceLocation();
            enableMyLocation();
        }

        // Listen for on marker click events
        mMap.setOnMarkerClickListener(this);
        // Configure the Product Summary View
        mMap.setInfoWindowAdapter(new ProductSummary());
        mMap.setOnInfoWindowClickListener(this);

        // Default map starting location
        LatLng mvb = new LatLng(51.456070226943865, -2.602992299931959);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mvb));

    }

    // May not need to check for permissions if only called after checking locationPermissionGranted
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
            } else {
                // Raise an error
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the grantResults array will be empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission has been granted
                    locationPermissionGranted = true;
                } else {
                    // TODO: Explain to user that the feature is unavailable because
                    //  the permissions have not been granted
                }
                return;
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
            enableMyLocation();
        } else if (shouldShowRequestPermissionRationale("FINE_LOCATION")) {
            // TODO: Explain to the user why the location permission is needed
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
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    lastKnownLocation = location;
                                    // Populate the map once location is found
                                    populateMap(mMap);
                                }
                            }
                        });
            }
        } catch (SecurityException e)  {
            // TODO: Implement appropriate error catching
        }
    }

    // Show the Product Summary when a marker is clicked
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    // Start the product page when clicked
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, ProductPageActivity.class);

        intent.putExtra("product", (Product) marker.getTag());
        intent.putExtra("contributor", ((Product) marker.getTag()).getContributor());
        intent.putExtra("profilePicId",((Product) marker.getTag()).getContributor().getProfileIcon());
        intent.putIntegerArrayListExtra("productPicId", (ArrayList<Integer>) ((Product) marker.getTag()).getImages());

        startActivity(intent);
    }


    // Populates the map with markers given a list of products and filter options
    private void populateMap(GoogleMap mMap) {
        mMap.clear();
        List<Product> products = ExampleData.getProducts();
        for (Product product : products) {
            LatLng coordinates = product.getLocation();
            Location productLocation = new Location("ManualProvider");
            productLocation.setLatitude(product.getLocation().latitude);
            productLocation.setLongitude(product.getLocation().longitude);
            float dist = lastKnownLocation.distanceTo(productLocation);
            Category productCategory = product.getCategory();
            if (dist <= maxDistanceRange && categoriesSelected.contains(productCategory)) {
                float hue = getHueFromRGB(productCategory.getCategoryColour());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(product.getName())
                        .snippet("by " + product.getContributor())
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                marker.setTag(product);
            }
        }
    }

    private float getHueFromRGB(int colour) {
        int red = Color.red(colour);
        int green = Color.green(colour);
        int blue = Color.blue(colour);
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        float hue = hsv[0];
        return hue;
    }

    // Checks if colour contrast is high enough
    // If false is returned use a light font colour instead of dark
    private boolean isColourTooDark(int colour) {
        float backgroundLuma = Color.luminance(colour);
        float textLuma = Color.luminance(DEFAULT_DARK_FONT);
        float ratio = (float) ((backgroundLuma + 0.05)/(textLuma + 0.05));
        if (ratio < MIN_CONTRAST_RATIO) {
            return true;
        }
        return false;
    }

    // Product Summary class
    private class ProductSummary implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;

        ProductSummary() {
            mWindow = getLayoutInflater().inflate(R.layout.product_summary_map, null);
        }

        private void renderInfoWindow(Marker marker) {
            Product product = (Product) marker.getTag();
            TextView title = (TextView) mWindow.findViewById(R.id.title);
            title.setText(product.getName());
            TextView contributor = (TextView) mWindow.findViewById(R.id.contributor);
            contributor.setText(product.getContributor().getName());
            TextView description = (TextView) mWindow.findViewById(R.id.description);
            description.setText(product.getDescription());
            ImageView photo = (ImageView) mWindow.findViewById(R.id.productimage);
            List<Integer> productPhotos = product.getImages();
            if (productPhotos.size() >= 1) {
                photo.setImageResource(productPhotos.get(0));
            } else {
                // use default
                photo.setImageResource(R.drawable.example_cup);
            }

        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderInfoWindow(marker);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            renderInfoWindow(marker);
            return mWindow;
        }
    }
}