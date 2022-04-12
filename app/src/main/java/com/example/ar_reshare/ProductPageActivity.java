package com.example.ar_reshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.Circle;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductPageActivity extends AppCompatActivity implements BackendController.BackendGetListingResultCallback,
        BackendController.BackendProfileResultCallback{

    private ImageView[] dots;
    private Product product;
    private User userProfile;
    private ArrayList<Bitmap> picList = new ArrayList<>();
    private ProductPicsSliderAdapter adapter;
    private CountDownLatch latch;
    private int TIMEOUT_IN_SECONDS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        // getting the stuff we need from previous page
        Intent i = getIntent();
        Product product = i.getParcelableExtra("product");
        Integer productID = i.getIntExtra("productID",1);
        Integer contributorID = i.getIntExtra("contributorID",1);
        Double lat = i.getDoubleExtra("lat",0);
        Double lng = i.getDoubleExtra("lng",0);

        latch = new CountDownLatch(2); // wait until it gets the product and the user information from the backend
        BackendController.getListingByID(productID,ProductPageActivity.this);
        BackendController.getProfileByID(0,1,contributorID,ProductPageActivity.this);
        //display a static map to show product's location
        displayMapPic(lat,lng);

        //display product name
        displayProductName(product);
        navProductName(product);
        //display product description
        displayProductDescription(product);

        //add a bookmark button
        bookmarkButton();

        //top left return arrow
        returnListener();

        //links to messaging page
//      messageButton(product,contributor,user, profilePicId);
        waitOnConditions();
    }

    @Override
    public void onBackendGetListingResult(boolean success, Product ListingResult) {
        this.product = ListingResult;
        if(success){
            latch.countDown();
        }
    }

    @Override
    public void onBackendProfileResult(boolean success, User userProfile) {
        this.userProfile = userProfile;
        if(success){
            latch.countDown();
        }
    }

    private void displayInfo(){
        //edit button
        //showEditIfUser(contributor,user);

        //display contributor's information
        displayProductContributor();

        // display product added time
        TextView addedTime = findViewById(R.id.addedtime);
        addedTime.setText(product.getDate() + "  added  ");

        //display product pics using slider
        picList.addAll(product.getPictures());
        displayProductPics(picList);
    }


    private void waitOnConditions() {
        // Create a new thread to wait for the conditions
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = latch.await(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                    if (success) {
                        // Any UI changes must be run on the UI Thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayInfo();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Failed to fetch the product. Please ensure you have access to an internet connection.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    System.out.println("CRASH");
                }
            }
        }).start();
    }

    private void showEditIfUser(User contributor, User user){
        if(contributor.getName().equals(user.getName())){
            ImageView edit = findViewById(R.id.edit);
            edit.setVisibility(View.VISIBLE);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductPageActivity.this, ModifyProduct.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void displayProductCondition(Product product){

    }

    private void displayProductCategory(Product product){
        ImageView category_pic = findViewById(R.id.category_pic);
        category_pic.setImageResource(product.getCategory().getCategoryIcon());
    }

    // navbar at the top to display the product name
    private void navProductName(Product product){
        TextView nav_name = findViewById(R.id.nav_name);
        nav_name.setText(product.getName());
    }

    // implement a top left return arrow that returns to previous page when clicked
    private void returnListener(){

        ImageView returnArrow = findViewById(R.id.returnArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void displayProductContributor(){
        TextView contributorName = findViewById(R.id.contributorName);
        CircleImageView contributorIcon = findViewById(R.id.circle);
        contributorName.setText(userProfile.getName());
        if (userProfile.getProfilePic() == null) {
            contributorIcon.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            contributorIcon.setImageBitmap(userProfile.getProfilePic());
        }
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
        if(user.getName().equals(contributor.getName())){ // check if the product's contributor is the user
            message.setVisibility(View.INVISIBLE); // hide the message button in this case
            TextView thanksMessage = findViewById(R.id.thanksForSharing);
            thanksMessage.setVisibility(View.VISIBLE);

        }
    }

    private void displayProductPics(ArrayList<Bitmap> picList){
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        adapter = new ProductPicsSliderAdapter(picList);
        int dotsCount = picList.size();
        displayPictureDots(dotsCount);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for(int i = 0; i < dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
            }

        });
    }

    private void displayPictureDots(int dotsCount){
        LinearLayout sliderDotsPanel = (LinearLayout) findViewById(R.id.SliderDots);
        dots = new ImageView[dotsCount];
        for(int i = 0; i < dotsCount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDotsPanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
    }

    private void displayMapPic(double lat, double lng){
        ImageView mapView = findViewById(R.id.map);
        String url = "https://maps.googleapis.com/maps/api/staticmap?center="+ lat + ","+ lng +
                "&zoom=15&size=400x400&markers=color:red|"+ lat + ","+ lng + "&key=" + getString(R.string.STATIC_MAP_KEY);
        Glide.with(this).load(url).into(mapView);
    }



}