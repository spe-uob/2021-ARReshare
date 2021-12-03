package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.URL;

public class ProductPageActivity extends AppCompatActivity {


    public static Drawable loadMapImage(String url){
        try{
            InputStream mapPic = (InputStream) new URL(url).getContent();
            Drawable map = Drawable.createFromStream(mapPic,"trial");
            return map;
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        // hardcoded a product

        User user = new User("Me", "", 1);
        Product cup = new Product("Fancy Cup","This is a fancy cup ", user, Category.OTHER,0,0);


        //display product name
        TextView productName = findViewById(R.id.productName);
        productName.setText(cup.getName());

        //display product description
        TextView description = findViewById(R.id.description);
        description.setText(cup.getDescription());
        // display product added time
        TextView addedTime = findViewById(R.id.addedtime);
        addedTime.setText(cup.getDate() + "  added  ");

        //add a bookmark button
        ImageView bookmark = (ImageView) findViewById(R.id.bookmark);
        bookmark.setTag(0);
        bookmark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bookmark.getTag().equals(0)){
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    bookmark.setTag(1);
                }else{
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    bookmark.setTag(0);
                }

            }
        });

        //display product pics using slider
        displayProductPics();

        //display a static map to show product's location
        ImageView mapView = findViewById(R.id.map);
        displayMapPic(mapView);
    }

    public void displayProductPics(){
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        SliderAdapter adapter;
        int list[] = new int[3];
        list[0] = getResources().getIdentifier("@drawable/cup",null,this.getPackageName());
        list[1] = getResources().getIdentifier("@drawable/cup2",null,this.getPackageName());
        list[2] = getResources().getIdentifier("@drawa ble/chaoba2",null,this.getPackageName());
        adapter = new SliderAdapter(list);
        viewPager.setAdapter(adapter);
    }

    public void displayMapPic(ImageView mapView){
        Glide.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=Berkeley,CA&zoom=14&size=400x400&key=AIzaSyBsn8QLFwcsXnxHf2ESE3HrXbch6lux3Ak").into(mapView);
    }
}