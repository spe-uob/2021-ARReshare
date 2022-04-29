package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ar_reshare.helpers.CameraPermissionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.ar.core.ArCoreApk;

public class SwipeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    BottomNavigationView bottomNavigationView;

    // Location related attributes:
    // Built-in class which provider current location
    protected FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    protected boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        //frameLayout = view.findViewById(R.id.frameLayout_wrapper);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Check permissions
        checkIfARAvailable();

        // Request location permissions if needed and get latest location
        getLocationPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        }
    }

    ChatListActivity chatListActivity = new ChatListActivity();
    MapsActivity mapsActivity = new MapsActivity();
    ProfileActivity profileActivity = new ProfileActivity();
    ARActivity arActivity = new ARActivity();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.map_menu_item:

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, mapsActivity).addToBackStack(null).commit();
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, mapsActivity).commit();
                return true;

//            case R.id.feed_menu_item:
//                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, mapsActivity).addToBackStack(null).commit();
//                //getSupportFragmentManager().beginTransaction().replace(R.id.container, secondFragment).commit();
//                return true;
//
            case R.id.ar_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, arActivity).addToBackStack(null).commit();
//                Intent intent1 = new Intent(FeedActivity.this, ARActivity.class);
//                startActivity(intent1);
                return true;
            case R.id.profile_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, profileActivity).addToBackStack(null).commit();
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, thirdFragment).commit();
                return true;
            case R.id.message_menu_item:
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, thirdFragment).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, chatListActivity).addToBackStack(null).commit();
                return true;
        }
        return false;
    }

    private void checkIfARAvailable() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (!availability.isSupported()) {
            Intent intent = new Intent(this, FallbackActivity.class);
            startActivity(intent);
            //arActivity = new FallbackActivity();
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
        } else {
            // If the location permission has not been granted already,
            // open a window requesting this permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

}