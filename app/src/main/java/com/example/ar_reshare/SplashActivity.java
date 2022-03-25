package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        Intent intent = new Intent(SplashActivity.this, ARActivity.class);

        // Check if user is logged in
        Optional<Account> account = AuthenticationService.isLoggedIn(getApplicationContext());
        if (account.isPresent()) {
            // Send request to backend to get a valid JWT token
            AuthenticationService.loginUser(getApplicationContext(), account.get(), new BackendClient.BackendCallback() {
                @Override
                public void onBackendResult(boolean result) {
                    if (result) proceed();
                    else forceLogin();
                }
            });
        } else forceLogin();

        ImageView appIcon = findViewById(R.id.appIcon);
        appIcon.animate().rotationBy(720).translationY(-400).setDuration(2000).start();
        TextView splashText = findViewById(R.id.splashText);
        splashText.animate().translationY(-700).setDuration(3000).start();
    }

    private void forceLogin() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginSignupActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }

    private void proceed() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, ARActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }
}