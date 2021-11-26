package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ar_reshare.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
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

        // Create dummy products and display them on the map
        List<Product> products = createDummyProducts();
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
                mMap.setOnMyLocationClickListener(this);
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
    public void onMyLocationClick(@NonNull Location location) {
        // Debugging
        System.out.println("Saved location = " + lastKnownLocation);
        System.out.println("From click = " + location);
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
            // Location has already been granted previously
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
        System.out.println("MARKER CLICKED");
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Link to the product page
    }


    /* ---------------------------------------------
            TEMPORARY CLASS AND HARDCODED DATA
       --------------------------------------------- */

    // Creates a list of dummy products for testing and development
    private List<Product> createDummyProducts () {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Fancy Cup", "This is a product.", "John", Category.OTHER,51.45120306024447, -2.5869936269149303));
        products.add(new Product("Java for Beginners Book", "This is a product.", "Artur", Category.BOOKS, 51.45599668866024, -2.6030781306216135));
        products.add(new Product("Pink Umbrella", "This is a product.", "Lingtao", Category.CLOTHING, 51.45416805430673, -2.591828561043675));
        products.add(new Product("Apple Pencil", "This is a product.", "Hellin", Category.ELECTRONICS,51.45864853294286, -2.5853638594577193));
        products.add(new Product("Meat", "This is a product.", "Ziqian", Category.FOOD, 51.45692540090406, -2.6081114869801714));
        products.add(new Product("Pink Headphones", "This is a product.", "Arafat", Category.ELECTRONICS, 51.459040571152514, -2.6022736036387366));
        return products;
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
            contributor.setText(product.getContributor());
            TextView description = (TextView) mWindow.findViewById(R.id.description);
            description.setText("This is a description of my product. " +
                    "It is really a great product. Feel free to message me to arrange a pickup. ");
            ImageView photo = (ImageView) mWindow.findViewById(R.id.productimage);
            photo.setImageResource(R.drawable.example_cup);
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