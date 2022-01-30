package com.example.ar_reshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

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
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
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

        // Get dummy products and display them on the map
        List<Product> products = ExampleData.getProducts();
        populateMap(mMap, products);

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
                    System.out.println("Location has been granted");
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
                                    System.out.println("onSuccess : " + lastKnownLocation);
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


    // Populates the map with markers given a list of products
    private void populateMap(GoogleMap mMap, List<Product> products) {
        for (Product product : products) {
            LatLng coordinates = product.getLocation();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(product.getName())
                    .snippet("by " + product.getContributor())
                    .icon(BitmapDescriptorFactory.defaultMarker(product.getCategory().getCategoryColour())));
            marker.setTag(product);
        }
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