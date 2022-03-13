package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ar_reshare.helpers.CameraPermissionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    ArrayList<Product> arrayList = new ArrayList<>();
    List<Product> productsList = ExampleData.getProducts();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;

    // Built-in class which provides current location
    private FusedLocationProviderClient fusedLocationClient;
    // The users last known location
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Done temporarily since not all users have icons, and not all products have images
        arrayList.add(productsList.get(1));
        arrayList.add(productsList.get(2));
        arrayList.add(productsList.get(3));
        arrayList.add(productsList.get(4));
        arrayList.add(productsList.get(5));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FeedRecyclerAdapter feedRecyclerAdapter = new FeedRecyclerAdapter(arrayList, lastKnownLocation);
        recyclerView.setAdapter(feedRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Request location permissions if needed and get latest location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        getDeviceLocation(feedRecyclerAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the grantResults array will be empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                locationPermissionGranted = true;
                System.out.println("Location has been granted");
            } else {
                // TODO: Explain to user that the feature is unavailable because
                //  the permissions have not been granted
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
            // TODO: Explain to the user why the location permission is needed
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
    private void getDeviceLocation(FeedRecyclerAdapter feedRecyclerAdapter) {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> task = fusedLocationClient.getLastLocation();
                task.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        lastKnownLocation = task.getResult();
                        feedRecyclerAdapter.updateDistances(lastKnownLocation);
                    }
                });
                } else {
                System.out.println("failed");
            }
    }
}