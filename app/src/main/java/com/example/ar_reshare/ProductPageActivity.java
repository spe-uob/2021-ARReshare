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

        // getting the stuff we need from previous page
        Intent i = getIntent();
        Product product = i.getParcelableExtra("product");
        User contributor = i.getParcelableExtra("contributor"); // the contributor of the current product
        User user = ExampleData.getUsers().get(0); // this is John
        Integer profilePicId = i.getIntExtra("profilePicId",R.drawable.arfi_profile_icon);
        List<Integer> productPicId = i.getIntegerArrayListExtra("productPicId");


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
        int[] picList = productPicId.stream().mapToInt(m -> m).toArray();
        displayProductPics(picList);

        //display a static map to show product's location
        displayMapPic(product.getLocation().latitude, product.getLocation().longitude);

        //top left return arrow
        returnListener();

        //links to messaging page
        messageButton(product,contributor,user, profilePicId);
    }

    // implement a top left return arrow that returns to previous page when clicked
    public void returnListener(){

        ImageView returnArrow = findViewById(R.id.returnArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    public void messageButton(Product product, User contributor, User user,Integer profilePicId){

        Button message = findViewById(R.id.messageButton);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this ,MessagingActivity.class);
                intent.putExtra("product", product);
                intent.putExtra("contributor", contributor);
                intent.putExtra("user",user);
                intent.putExtra("profilePicId", profilePicId);

                startActivity(intent);
            }
        });
    }

    public void displayProductPics(int[] productPicId){
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        SliderAdapter adapter;
        adapter = new SliderAdapter(productPicId);
        viewPager.setAdapter(adapter);
    }

    public void displayMapPic(double lat, double lng){
        ImageView mapView = findViewById(R.id.map);
        Glide.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=%2051.454513,-2.58791&zoom=10&size=400x400&key=AIzaSyAFWHH-yjENxp6a7kQUeFfLjWcbGBuuM6Y").into(mapView);

    }
}