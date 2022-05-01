package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;

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
    protected boolean cameraPermissionGranted;

    // Swiping gestures variables and constants
    private float x1, x2, y1, y2;
    private final int TOUCH_OFFSET = 100;
    private final int TAP_OFFSET = 10;
    private boolean touchedDown = false;
    private boolean moved = false;

    Fragment chatListActivity = new ChatListActivity();
    Fragment mapsActivity = new MapsActivity();
    Fragment profileActivity = new ProfileActivity();
    Fragment arActivity = new ARActivity();
    Fragment feedActivity = new FeedActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        // Check permissions
        checkIfARAvailable();

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        //frameLayout = view.findViewById(R.id.frameLayout_wrapper);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.ar_menu_item);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, arActivity).addToBackStack(null).commit();

        // Request location permissions if needed and get latest location
        getLocationPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            System.out.println("DONT HAVE CAMERA");
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        } else {
            System.out.println("HAVE CAMERA");
            cameraPermissionGranted = true;
        }

        // TODO: Add on request permission result check
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new MapsActivity()).addToBackStack(null).commit();
                return true;
            case R.id.feed_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new FeedActivity()).addToBackStack(null).commit();
                return true;
            case R.id.ar_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ARActivity()).addToBackStack(null).commit();
                return true;
            case R.id.profile_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ProfileActivity()).addToBackStack(null).commit();
                return true;
            case R.id.message_menu_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ChatListActivity()).addToBackStack(null).commit();
                return true;
        }
        return false;
    }

    private void checkIfARAvailable() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (!availability.isSupported()) {
            arActivity = new FallbackActivity();
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

    // Logic for handling swiping gestures between activities
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchedDown = true;
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                touchedDown = false;
                int currentId = bottomNavigationView.getSelectedItemId();
                // Swiping to the right
                if (Math.abs(x1)+ TOUCH_OFFSET < Math.abs(x2)) {
                    switch (currentId) {
                        case R.id.feed_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new MapsActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.map_menu_item);
                            break;
                        case R.id.ar_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new FeedActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.feed_menu_item);
                            break;
                        case R.id.profile_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, arActivity).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.ar_menu_item);
                            break;
                        case R.id.message_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ProfileActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);
                            break;
                    }
                // Swiping to the left
                } else if((Math.abs(x1) > Math.abs(x2)+ TOUCH_OFFSET)) {
                    switch (currentId) {
                        case R.id.map_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new FeedActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.feed_menu_item);
                            break;
                        case R.id.feed_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, arActivity).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.ar_menu_item);
                            break;
                        case R.id.ar_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ProfileActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);
                            break;
                        case R.id.profile_menu_item:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_wrapper, new ChatListActivity()).addToBackStack(null).commit();
                            bottomNavigationView.setSelectedItemId(R.id.message_menu_item);
                            break;
                    }
                }
                moved = false;
                break;
        }
        return false;
    }

}