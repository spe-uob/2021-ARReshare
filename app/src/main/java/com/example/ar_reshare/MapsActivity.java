package com.example.ar_reshare;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

import org.xmlpull.v1.XmlPullParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends Fragment implements
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
    private int tempDistanceRange = MAX_DISTANCE;

    // Category Filtering
    private final int UNCHECKED_CHIP_COLOUR = Color.parseColor("#dbdbdb");
    private Set<Category> categoriesSelected = new HashSet<>(Category.getCategories());
    private Set<Category> tempCategories = new HashSet<>(categoriesSelected);

    // Colour constants for accessibility needs - changing font colour based on contrast
    private final float MIN_CONTRAST_RATIO = 4.5f;
    private final int DEFAULT_DARK_FONT = Color.parseColor("#363636");

    // The list of products
    private List<Product> products;
    private CountDownLatch readyLatch;
    private int TIMEOUT_IN_SECONDS = 10;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Make the map wait on the following three conditions
        // 1. Device location is ready
        // 2. Products have been received from backend
        // 3. GoogleMap is ready
        // Once these conditions are met the map can proceed to be populated
        readyLatch = new CountDownLatch(3);
        getLatestProducts();
        View view = inflater.inflate(R.layout.activity_maps, container, false);
//        binding = ActivityMapsBinding.inflate(getActivity().getLayoutInflater(),container,false);
//        View view = binding.getRoot();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton filterButton = view.findViewById(R.id.filterMapButton);
        setupFilterWindow(filterButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocationPermission();

        // Wait to populate the map until conditions are fulfilled
        waitOnConditions();
        return view;
    }



    private void getLatestProducts() {
        BackendController.searchListings(0, 100, new BackendController.BackendSearchResultCallback() {
            @Override
            public void onBackendSearchResult(boolean success, List<Product> searchResults) {
                if (success) {
                    products = searchResults;
                    readyLatch.countDown();
                    System.out.println(readyLatch.getCount());
                }
            }
        });
    }

    private void waitOnConditions() {
        // Create a new thread to wait for the conditions
        new Thread(() -> {
            try {
                boolean success = readyLatch.await(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                if (success) {
                    // When using runOnUiThread, catch exceptions which may occur if fragment is changed
                    try {
                        // Any UI changes must be run on the UI Thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateMap(mMap);
                            }
                        });
                    } catch (Exception e) {}
                } else {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Failed to fetch your location or the products from the server. Please ensure you have access to an internet connection.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {}
                }
            } catch (InterruptedException e) {
                System.out.println("CRASH");
            }
        }).start();
    }

    // Setup filter results window
    private void setupFilterWindow(ImageButton filterButton) {
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
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

    // Unselect all filters
    @SuppressWarnings("SuspiciousMethodCalls")
    private void unselectFilter(ChipGroup allChips) {
        List<Integer> chipsList = allChips.getCheckedChipIds();
        for (Integer chipID : chipsList) {
            Chip chip = allChips.findViewById(chipID);
            chip.setChecked(false);
            chip.setChipBackgroundColor(ColorStateList.valueOf(UNCHECKED_CHIP_COLOUR));
            chip.setTextColor(DEFAULT_DARK_FONT);
            //noinspection SuspiciousMethodCalls
            tempCategories.remove(chip.getTag());
        }
    }

    // Setup category filtering UI
    private void setupCategoryChipGroup(View filterWindow) {
        ChipGroup filterCategories = filterWindow.findViewById(R.id.filterCategoryChipGroup);

        List<Category> categories = Category.getCategories();
        for (Category category : categories) {
            Chip categoryChip = (Chip) getLayoutInflater().inflate(R.layout.single_filter_chip, filterCategories, false);
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
        Button filterUnselectButton = filterWindow.findViewById(R.id.filterUnselect);
        filterUnselectButton.setOnClickListener(v ->
                unselectFilter(filterWindow.findViewById(R.id.filterCategoryChipGroup)));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }
// When leaving the Map Activity always animate sliding down

    public void finish() {
        super.getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
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

        // Decrement the latch
        readyLatch.countDown();
    }

    // May not need to check for permissions if only called after checking locationPermissionGranted
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
//                // If request is cancelled, the grantResults array will be empty
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Location permission has been granted
//                    locationPermissionGranted = true;
//                } else {
//                    // TODO: Explain to user that the feature is unavailable because
//                    //  the permissions have not been granted
//                }
//                return;
//        }
//    }

    // Request location permissions from the device. We will receive a callback
    // to onRequestPermissionsResult with the results.
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Get the most recent location of the device
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    lastKnownLocation = location;
                                    // Decrement the latch to signal user location is ready
                                    readyLatch.countDown();
                                    System.out.println(readyLatch.getCount());
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
        return false;
    }

    // Start the product page when clicked
    @Override
    public void onInfoWindowClick(Marker marker) {
        Product product = (Product)marker.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("contributorID",product.getContributorID());
        bundle.putString("productName",product.getName());
        bundle.putString("productDescription",product.getDescription());
        bundle.putInt("productID",product.getId());
        bundle.putDouble("lat", product.getCoordinates().latitude);
        bundle.putDouble("lng",product.getCoordinates().longitude);
        bundle.putString("postcode",product.getPostcode());
        bundle.putBoolean("isSaved", product.isSavedByUser());
        ProductPageActivity productFragment = new ProductPageActivity();
        productFragment.setArguments(bundle);
        productFragment.setIsFromFeed(false);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_wrapper,productFragment).addToBackStack(null).commit();
    }


    // Populates the map with markers given a list of products and filter options
    private void populateMap(GoogleMap mMap) {
        mMap.clear();
        System.out.println("POPULATE MAP CALLED");
        for (Product product : products) {
            System.out.println(product.getName());
            System.out.println(product.getPostcode());
            System.out.println(product.getCoordinates());
            getProductContributor(product);
        }
    }

    // Downloads the profile of the contributor of the product and proceeds to show it on the map
    private void getProductContributor(Product product) {
        BackendController.getProfileByID(0, 1, product.getContributorID(), new BackendController.BackendProfileResultCallback() {
            @Override
            public void onBackendProfileResult(boolean success, User userProfile) {
                product.setContributor(userProfile);
                // When using runOnUiThread, catch exceptions which may occur if fragment is changed
                try {
                    getActivity().runOnUiThread(() -> addMarker(product));
                } catch (Exception e) {}
            }
        });
    }

    // Adds a new marker to the map if it meets the current filter
    private void addMarker(Product product) {
        LatLng coordinates = product.getCoordinates();
        Location productLocation = new Location("ManualProvider");
        productLocation.setLatitude(coordinates.latitude);
        productLocation.setLongitude(coordinates.longitude);
        float dist = lastKnownLocation.distanceTo(productLocation);
        Category productCategory = Category.getCategoryById(product.getCategoryID());
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

        private void renderInfoWindow(Product product) {
            User user = product.getContributor();
            TextView title = (TextView) mWindow.findViewById(R.id.title);
            title.setText(product.getName());
            TextView contributor = (TextView) mWindow.findViewById(R.id.contributor);
            if (user != null) contributor.setText(user.getName());
            else contributor.setText("");
            TextView description = (TextView) mWindow.findViewById(R.id.description);
            description.setText(product.getDescription());
            ImageView photo = (ImageView) mWindow.findViewById(R.id.productimage);
            photo.setImageResource(R.drawable.example_cup);

            // Show photo
            Bitmap productPhoto = product.getMainPic();
            if (productPhoto != null) photo.setImageBitmap(productPhoto);
            else photo.setImageResource(R.drawable.example_cup);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            Product product = (Product) marker.getTag();
            renderInfoWindow(product);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Product product = (Product) marker.getTag();
            renderInfoWindow(product);
            return mWindow;
        }
    }
}