package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Product cup = new Product("Cup","This is a fancy cup", "Me");
        TextView productName = findViewById(R.id.productName);
        productName.setText(cup.getName());
        TextView description = findViewById(R.id.description);
        description.setText(cup.getDescription());

        //ImageView productPic = findViewById(R.id.productPic);
        //int imageResource = getResources().getIdentifier("@drawable/cup",null, this.getPackageName());
        //productPic.setImageResource(imageResource);

        SliderAdapter adapter;
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        int list[] = new int[2];
        list[0] = getResources().getIdentifier("@drawable/cup",null,this.getPackageName());
        list[1] = getResources().getIdentifier("@drawable/cup2",null,this.getPackageName());

        adapter = new SliderAdapter(list);
        viewPager.setAdapter(adapter);

    }
}