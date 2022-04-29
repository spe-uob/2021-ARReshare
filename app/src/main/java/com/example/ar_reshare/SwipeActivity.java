package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SwipeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        //frameLayout = view.findViewById(R.id.frameLayout_wrapper);
        bottomNavigationView.setOnItemSelectedListener(this);
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
}