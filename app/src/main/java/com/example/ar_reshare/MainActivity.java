package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    float x1, x2, y1, y2;
    private static final int OFFSET = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (Math.abs(x1)+OFFSET < Math.abs(x2)) {
                    Intent i = new Intent(MainActivity.this, FeedActivity.class);
                    startActivity(i);
                } else if((Math.abs(x1) > Math.abs(x2)+OFFSET)) {
                    Intent i = new Intent(MainActivity.this, ProductPageActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }
}