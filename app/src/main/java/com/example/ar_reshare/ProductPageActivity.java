package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductPageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        // hardcoded a product

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        Product product = extras.getParcelable("product");
        User contributor = extras.getParcelable("contributor"); // the contributor of the current product
        User user = ExampleData.getUsers().get(0); // this is John
        Integer profilePicId = extras.getInt("profilePicId");

        //ArrayList<Integer> productPicIdList = (ArrayList<Integer>) extras.getIntegerArrayList("productPicId");
        //System.out.println(productPicIdList);
        //display product name
        displayProductName(product);

        //display product description
        displayProductDescription(product);

        //display contributor's information
        displayProductContributor(contributor,profilePicId);

        // display product added time
        TextView addedTime = findViewById(R.id.addedtime);
        addedTime.setText(product.getDate() + "  added  ");

        //add a bookmark button
        bookmarkButton();

        //display product pics using slider
        displayProductPics();

        //display a static map to show product's location
        displayMapPic();

        //top left return arrow
        returnToMapListener();

        //links to messaging page
        messageButton(product,contributor,user);
    }

    // implement a top left return arrow that returns to previous page when clicked
    public void returnToMapListener(){
        ImageView returnArrow = findViewById(R.id.returnArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void displayProductContributor(User contributor, int id){
        TextView contributorName = findViewById(R.id.contributorName);
        CircleImageView contributorIcon = findViewById(R.id.circle);

        contributorName.setText(contributor.getName());
        contributorIcon.setImageResource(id);
    }


    public void displayProductName(Product product){
        TextView productName = findViewById(R.id.productName);
        productName.setText(product.getName());
    }

    public void displayProductDescription(Product product){
        TextView description = findViewById(R.id.description);
        description.setText(product.getDescription());
    }

    public void bookmarkButton(){
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
    }

    public void messageButton(Product product, User contributor, User user){
        Button message = findViewById(R.id.messageButton);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this ,MessagingActivity.class);
                intent.putExtra("product", product);
                intent.putExtra("contributor", contributor);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
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

    public void displayMapPic(){
        ImageView mapView = findViewById(R.id.map);
        Glide.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=Bristol,CA&zoom=14&size=400x400&key=AIzaSyBsn8QLFwcsXnxHf2ESE3HrXbch6lux3Ak").into(mapView);
    }
}