package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        // hardcoded a product
        Product cup = new Product("Fancy Cup","This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup aaaaaaaaaaaaaaaaa This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup This is a fancy cup   ", "Me",0,0);

        //display product name
        TextView productName = findViewById(R.id.productName);
        productName.setText(cup.getName());

        //display product description
        TextView description = findViewById(R.id.description);
        description.setText(cup.getDescription());
        // display product added time
        TextView addedTime = findViewById(R.id.addedtime);
        addedTime.setText(cup.getDate() + "  added  ");

        //add a favorite button
        ImageView favorite = (ImageView) findViewById(R.id.favorite);
        favorite.setTag(0);
        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(favorite.getTag().equals(0)){
                    favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    favorite.setTag(1);
                }else{
                    favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    favorite.setTag(0);
                }

            }
        });

        //display product pics using slider
        SliderAdapter adapter;
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        int list[] = new int[3];
        list[0] = getResources().getIdentifier("@drawable/cup",null,this.getPackageName());
        list[1] = getResources().getIdentifier("@drawable/cup2",null,this.getPackageName());
        list[2] = getResources().getIdentifier("@drawable/chaoba2",null,this.getPackageName());

        adapter = new SliderAdapter(list);
        viewPager.setAdapter(adapter);

    }
}