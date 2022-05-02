package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductPageActivity extends Fragment implements BackendController.BackendGetListingResultCallback,
        BackendController.BackendProfileResultCallback{

    private ImageView[] dots;
    private String[] imageUriList;
    private Product product;
    private String postcode;
    private User userProfile;
    private ArrayList<Bitmap> picList = new ArrayList<>();
    private ProductPicsSliderAdapter adapter;
    private CountDownLatch latch;
    private int TIMEOUT_IN_SECONDS = 5;
    private View view;
    private ImageView feedBookmarkButton;
    private Boolean isFromFeed = false;
    private Boolean isSaved;

    public void setFeedBookmarkButton(ImageView feedBookmarkButton) {
        this.feedBookmarkButton = feedBookmarkButton;
    }

    public void setIsFromFeed(Boolean isFromFeed){
        this.isFromFeed = isFromFeed;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_product_page, container, false);
        //hide the bottom navigation bar
        SwipeActivity activity = (SwipeActivity) getActivity();
        activity.setNavigationVisibility(false);
        // getting all the stuff we need from previous page
        Integer productID = getArguments().getInt("productID", 1);
        String productName = getArguments().getString("productName");
        String productDescription = getArguments().getString("productDescription");
        Integer contributorID = getArguments().getInt("contributorID", 1);
        Double lat = getArguments().getDouble("lat",0);
        Double lng = getArguments().getDouble("lng",0);
        postcode = getArguments().getString("postcode");
        isSaved = getArguments().getBoolean("isSaved",false);
        latch = new CountDownLatch(2); // wait until it gets the product and the user information from the backend
        BackendController.getListingByID(productID,ProductPageActivity.this);
        BackendController.getProfileByID(0,1,contributorID,ProductPageActivity.this);

        //display a static map to show product's location
        displayMapPic(lat,lng);

        //display product name
        displayProductName(productName);
        navProductName(productName);
        //display product description
        displayProductDescription(productDescription);

        //add a bookmark button
        bookmarkButton();

        //top left return arrow
        returnListener();

        //links to messaging page
//      messageButton(product,contributor,user, profilePicId);

        waitOnConditions();
        return view;
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
        showEditIfUser();

        displayProductCondition(product);
        displayProductCategory(product);

        //display contributor's information
        displayProductContributor();

        // display product added time or modified time
        if(product.getModificationDate() == null){
            System.out.println(product.getModificationDate());
            TextView addedTime = view.findViewById(R.id.time);
            String[] time = MessagingActivity.convertDate(product.getCreationDate());
            addedTime.setText(time[3] + " " + time[2] + "-" + time[1] + " " +time[5] +"  added ");
        } else {
            TextView modifiedTime = view.findViewById(R.id.time);
            String[] time = MessagingActivity.convertDate(product.getModificationDate());
            modifiedTime.setText(time[3] + " " + time[2] + "-" + time[1] + " " +time[5] + "  modified ");
        }

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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayInfo();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(),
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

    //convert the image bitmap to URI for passing to ModifyProduct page
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //TODO: only show these buttons if user's own product
    private void showEditIfUser(){
        ImageView edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(),v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.product_setting, popupMenu.getMenu());
                MenuItem delete = popupMenu.getMenu().getItem(1);
                SpannableString spannable = new SpannableString("Delete");
                spannable.setSpan(new ForegroundColorSpan(Color.RED),0,spannable.length(),0);
                delete.setTitle(spannable);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "Edit":
                                item.setEnabled(false);
                                int picCount = product.getPictures().size();
                                imageUriList = new String[picCount];
                                for (int i = 0; i < picCount; i++) {
                                    imageUriList[i] = getImageUri(getActivity().getApplicationContext(),product.getPictures().get(i)).toString();
                                }
                                Intent intent = new Intent(getActivity(), ModifyProduct.class);
                                intent.putExtra("productID", product.getId());
                                intent.putExtra("productName", product.getName());
                                intent.putExtra("productDescription", product.getDescription());
                                intent.putExtra("categoryID",product.getCategoryID());
                                intent.putExtra("condition",product.getCondition());
                                intent.putExtra("postcode", postcode);
                                intent.putExtra("images",imageUriList);
                                startActivity(intent);
                                return true;
                            case "Delete":
                                item.setEnabled(false);
                                confirmToDelete();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void confirmToDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to delete this product?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            BackendController.closeListing(product.getId(), new BackendController.BackendCallback() {
                                @Override
                                public void onBackendResult(boolean success, String message) {
                                    if(success){
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Product deleted successfully!",
                                                Toast.LENGTH_LONG).show();
                                        // go back to the main page when finished
                                        //startActivity(new Intent(ProductPageActivity.this, ARActivity.class));
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Delete product");
        alertDialog.show();
    }

    private void displayProductCondition(Product product){
        TextView conditionView = view.findViewById(R.id.condition);
        // Capitalise the first letter
        String condition = product.getCondition().substring(0,1).toUpperCase() + product.getCondition().substring(1);
        conditionView.setText("Condition: " + condition);
    }

    private void displayProductCategory(Product product){
        ImageView category_pic = view.findViewById(R.id.category_pic);
        Integer categoryIcon = Category.getCategoryById(product.getCategoryID()).getCategoryIcon();
        category_pic.setImageResource(categoryIcon);
    }

    // navbar at the top to display the product name
    private void navProductName(String productName){
        TextView nav_name = view.findViewById(R.id.nav_name);
        nav_name.setText(productName);
    }

    // implement a top left return arrow that returns to previous page when clicked
    private void returnListener(){

        ImageView returnArrow = view.findViewById(R.id.returnArrow);
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                SwipeActivity activity = (SwipeActivity) getActivity();
                activity.setNavigationVisibility(true);
            }
        });
    }

    public void displayProductContributor(){
        TextView contributorName = view.findViewById(R.id.contributorName);
        CircleImageView contributorIcon = view.findViewById(R.id.circle);
        contributorName.setText(userProfile.getName());
        if (userProfile.getProfilePic() == null) {
            contributorIcon.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            contributorIcon.setImageBitmap(userProfile.getProfilePic());
        }
    }


    public void displayProductName(String productName){
        TextView productNameText = view.findViewById(R.id.productName);
        productNameText.setText(productName);
    }

    public void displayProductDescription(String productDescription){
        TextView descriptionText = view.findViewById(R.id.description);
        descriptionText.setText(productDescription);
    }

    public void bookmarkButton(){
        ImageView bookmark = view.findViewById(R.id.bookmark);
        if(isFromFeed){
            bookmark.setTag(feedBookmarkButton.getTag());
            if(bookmark.getTag().equals(0)){
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

            }else{
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
            }
        }else{
            if(isSaved){
                bookmark.setTag(1);
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
            }else{
                bookmark.setTag(0);
                bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
            }
        }

        bookmark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bookmark.getTag().equals(0)){
                    try {
                        BackendController.createSavedListing(product.getId(), (success, message) -> {
                            System.out.println(message);
                            if (success) {
                                System.out.println("createSavedListing callback success");
                            } else {
                                System.out.println("createSavedListing callback failed");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    bookmark.setTag(1);
                    if(isFromFeed){
                        feedBookmarkButton.setImageResource(R.drawable.filled_white_bookmark);
                        feedBookmarkButton.setTag(1);
                    }

                }else{
                    try {
                        BackendController.deleteSavedListing(product.getId(), (success, message) -> {
                            System.out.println(message);
                            if (success) {
                                System.out.println("deleteSavedListing callback success");
                            } else {
                                System.out.println("deleteSavedListing callback failed");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    bookmark.setTag(0);
                    if(isFromFeed){
                        feedBookmarkButton.setImageResource(R.drawable.white_bookmark);
                        feedBookmarkButton.setTag(0);
                    }
                }

            }
        });
    }

    public void messageButton(Product product, User contributor, User user,Integer profilePicId){

        Button message = view.findViewById(R.id.messageButton);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ProductPageActivity.this ,MessagingActivity.class);
//                intent.putExtra("product", product);
//                intent.putExtra("contributor", contributor);
//                intent.putExtra("user",user);
//                intent.putExtra("profilePicId", profilePicId);
//                startActivity(intent);
            }
        });
        if(user.getName().equals(contributor.getName())){ // check if the product's contributor is the user
            message.setVisibility(View.INVISIBLE); // hide the message button in this case
            TextView thanksMessage = view.findViewById(R.id.thanksForSharing);
            thanksMessage.setVisibility(View.VISIBLE);

        }
    }

    private void displayProductPics(ArrayList<Bitmap> picList){
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        adapter = new ProductPicsSliderAdapter(picList);
        int dotsCount = picList.size();
        displayPictureDots(dotsCount);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for(int i = 0; i < dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.active_dot));
            }

        });
    }

    private void displayPictureDots(int dotsCount){
        LinearLayout sliderDotsPanel = view.findViewById(R.id.SliderDots);
        dots = new ImageView[dotsCount];
        for(int i = 0; i < dotsCount; i++){
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDotsPanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.active_dot));
    }

    private void displayMapPic(double lat, double lng){
        ImageView mapView = view.findViewById(R.id.map);
        String url = "https://maps.googleapis.com/maps/api/staticmap?center="+ lat + ","+ lng +
                "&zoom=15&size=400x400&markers=color:red|"+ lat + ","+ lng + "&key=" + getString(R.string.STATIC_MAP_KEY);
        Glide.with(this).load(url).into(mapView);
    }

}