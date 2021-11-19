package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ar_reshare.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    private FusedLocationProviderClient fusedLocationClient;
    public Location lastKnownLocation;

    // TODO: Clean up code
    // TODO: Remove any unnecessary functions, variables and constants
    // TODO: Change access modifiers for some attributes and functions
    // TODO: Add more comments explaining logic and functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //lastKnownLocation = new LatLng(0, 0);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
    }

    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            getDeviceLocation();
            // TODO: Consider moving the two lines below into the enableMyLocation()
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            enableMyLocation();
        }

        // Listen for on marker click events
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new ProductSummary());
        mMap.setOnInfoWindowClickListener(this);

        List<Product> products = createDummyProducts();
        populateMap(mMap, products);

        LatLng mvb = new LatLng(51.456070226943865, -2.602992299931959);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mvb));

        // Debugging
        System.out.println("onMapReady: " + lastKnownLocation);
        System.out.println("This class = " + this);
    }

    // TODO: May not need to check for permissions if only called after checking locationPermissionGranted
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    locationPermissionGranted = true;
                    System.out.println("Location has been granted");
                } else {
                    // TODO:
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            System.out.println("Location already granted");
            enableMyLocation();
        } else if (shouldShowRequestPermissionRationale("FINE_LOCATION")) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
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
            System.out.println("Security Error");
        }
        System.out.println("getLocation: " + lastKnownLocation);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setSnippet("Clicked");
        marker.showInfoWindow();
        System.out.println("clicked on marker");
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("Go to product page");
    }


    /* ---------------------------------------------
            TEMPORARY CLASS AND HARDCODED DATA
       --------------------------------------------- */

    // TODO: Create a method which sends a request to the backend and receives the list of products nearby
    // Creates a list of dummy products for testing and development
    private List<Product> createDummyProducts () {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Fancy Cup", "John", 51.45120306024447, -2.5869936269149303));
        products.add(new Product("Magic Pen", "Artur", 51.45599668866024, -2.6030781306216135));
        products.add(new Product("Pink Umbrella", "Lingtao", 51.45416805430673, -2.591828561043675));
        products.add(new Product("Apple Pencil", "Hellin", 51.45864853294286, -2.5853638594577193));
        products.add(new Product("Meat", "Ziqian", 51.45692540090406, -2.6081114869801714));
        products.add(new Product("Pink Headphones", "Arafat", 51.459040571152514, -2.6022736036387366));
        return products;
    }

    // Populates the map with markers given a list of products
    private void populateMap(GoogleMap mMap, List<Product> products) {
        for (Product product : products) {
            LatLng coordinates = new LatLng(product.lat, product.lng);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(product.name)
                    .snippet("by " + product.contributor));
            marker.setTag(product);
        }
    }



    private class ProductSummary implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        //private final View mContents;

        ProductSummary() {
            mWindow = getLayoutInflater().inflate(R.layout.product_summary_map, null);
        }

        private void renderInfoWindow(Marker marker) {
            Product product = (Product) marker.getTag();
            TextView title = (TextView) mWindow.findViewById(R.id.title);
            title.setText(product.name);
            TextView contributor = (TextView) mWindow.findViewById(R.id.contributor);
            contributor.setText(product.contributor);
            TextView description = (TextView) mWindow.findViewById(R.id.description);
            description.setText("This is a description of my product. " +
                    "It is really a great product. Feel free to message me to arrange a pickup. ");
            ImageView photo = (ImageView) mWindow.findViewById(R.id.productimage);
            photo.setImageDrawable(getDrawable(R.drawable.example_cup));

            //((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
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

    // TODO: Add appropriate access modifiers
    // TODO: Create getter and setter methods
    // TODO: Consult with the rest of the team to create a common product, user/contributor objects
    // Temporary class for development and testing
    private class Product {
        public String name;
        public String contributor;
        public double lat;
        public double lng;

        Product(String name, String contributor, double lat, double lng) {
            this.name = name;
            this.contributor = contributor;
            this.lat = lat;
            this.lng = lng;
        }
    }
}