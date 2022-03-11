package com.example.ar_reshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    PreviewView previewView;
    boolean cameraPermissionGranted;

    // Swiping gestures variables and constants
    private float x1, x2, y1, y2;
    private final int TOUCH_OFFSET = 100;
    private final int TAP_OFFSET = 10;
    private boolean touchedDown = false;
    private boolean moved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCameraPermission();

        previewView = findViewById(R.id.previewView);

        if (cameraPermissionGranted) {

            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    startCameraX(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, getExecutor());

        }

    }

    // Request camera permissions from the device. We will receive a callback
    // to onRequestPermissionsResult with the results.
    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission has already been granted previously
            cameraPermissionGranted = true;
        } else {
            // If the camera permission has not been granted already,
            // open a window requesting this permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

    // Logic for handling swiping gestures between activities
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent){
        TextView swipingClue = findViewById(R.id.swipingClueMain);
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
                if (Math.abs(x1)+ TOUCH_OFFSET < Math.abs(x2)) {
                    Intent i = new Intent(MainActivity.this, FeedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else if((Math.abs(x1) > Math.abs(x2)+ TOUCH_OFFSET)) {
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if ((y1 - y2 > TOUCH_OFFSET) || (Math.abs(x2-x1) < TAP_OFFSET && Math.abs(y2-y1) < TAP_OFFSET && !moved)) {
                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
                swipingClue.setVisibility(View.INVISIBLE);
                moved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (touchedDown) {
                    if (Math.abs(x2 - x1) > TAP_OFFSET || Math.abs(y2 - y1) > TAP_OFFSET) {
                        moved = true;
                    }
                    if (Math.abs(x1)+ TOUCH_OFFSET < Math.abs(x2)) {
                        swipingClue.setText("Feed >>>");
                        float diff = ((x2 - x1) - TOUCH_OFFSET)/(2.5f*TOUCH_OFFSET);
                        swipingClue.setPadding(Math.round(diff*TOUCH_OFFSET), 0, 0, 0);
                        if (diff > 1) diff = 1.0f;
                        else if (diff < 0.5) diff = 0.25f;
                        swipingClue.setAlpha(diff);
                        swipingClue.setVisibility(View.VISIBLE);
                    } else if((Math.abs(x1) > Math.abs(x2)+ TOUCH_OFFSET)) {
                        swipingClue.setText("<<< Profile");
                        float diff = ((x1 - x2) - TOUCH_OFFSET)/(2.5f*TOUCH_OFFSET);
                        swipingClue.setPadding(0, 0, Math.round(diff*TOUCH_OFFSET), 0);
                        if (diff > 1) diff = 1.0f;
                        else if (diff < 0.5) diff = 0.25f;
                        swipingClue.setAlpha(diff);
                        swipingClue.setVisibility(View.VISIBLE);
                    } else if (y1 - y2 > TOUCH_OFFSET) {
                        swipingClue.setText("^ Map ^");
                        swipingClue.setVisibility(View.VISIBLE);
                        float diff = ((y1 - y2) - TOUCH_OFFSET)/(2.5f*TOUCH_OFFSET);
                        swipingClue.setPadding(0, 0, 0, Math.round(diff*TOUCH_OFFSET));
                        if (diff > 1) diff = 1.0f;
                        else if (diff < 0.5) diff = 0.25f;
                        swipingClue.setAlpha(diff);
                        swipingClue.setVisibility(View.VISIBLE);
                    } else {
                        swipingClue.setVisibility(View.INVISIBLE);
                    }
                } else {
                    swipingClue.setVisibility(View.INVISIBLE);
                    swipingClue.setText("");
                }
                break;
        }
        return false;
    }
}