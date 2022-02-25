package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 4000); // change to main activity after 5 secs

        ImageView appIcon = findViewById(R.id.appIcon);
        appIcon.animate().rotationBy(720).translationY(-400).setDuration(2000).start();
        TextView splashText = findViewById(R.id.splashText);
        splashText.animate().translationY(-700).setDuration(3000).start();
    }
}