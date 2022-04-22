package com.example.ar_reshare;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ModifyProduct extends AppCompatActivity implements addPhotoDialog.NoticeDialogListener,BackendController.BackendCallback {

    private ArrayList<Uri> uploadedImages = new ArrayList<>();
    private UploadImageAdapter adapter;
    private Integer productID;
    private final String CAMERA = "camera";
    private final String GALLERY = "gallery";
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<String> galleryActivityResultLauncher;
    private Uri photoUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);
        Intent intent = getIntent();
        productID = intent.getIntExtra("productID",1);
        String productName = intent.getStringExtra("productName");
        String productDescription = intent.getStringExtra("productDescription");
        Integer categoryID = intent.getIntExtra("categoryID",1);
        String condition = intent.getStringExtra("condition");
        String postcode = intent.getStringExtra("postcode");
        //get all the previous images of the product
        String[] images = intent.getStringArrayExtra("images");
        for (String image : images) {
            uploadedImages.add(Uri.parse(image));
        }
        adapter = new UploadImageAdapter(uploadedImages);

        //Camera intent launcher, add the picture to the product pics list once finished
        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            adapter.addItem(photoUri);
                        }else{
                            CameraHelper.deleteImageFile();//delete the file if failed
                        }
                    }
                }
        );

        //Gallery intent launcher, allowing users to select multiple pictures at a time
        galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
                    @Override
                    public void onActivityResult(List<Uri> uri) {
                        adapter.addAllItems(uri);
                    }
                }
        );

        displayStringText(productName,productDescription,postcode);
        addImageListener();
        confirmListener();
        uploadedImageView();
        categoryDropdown(categoryID);
        conditionDropdown(condition);
        returnListener();
    }



    // used a recycler view to display the images chosen by users
    private void uploadedImageView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        RecyclerView uploaded_image_list = findViewById(R.id.uploaded_image_list);
        uploaded_image_list.setLayoutManager(layoutManager);
        uploaded_image_list.setAdapter(adapter);

    }

    //display product's name, description and postcode
    private void displayStringText(String name, String description, String postcode){
        EditText productNameText = findViewById(R.id.add_product_name);
        productNameText.setText(name);
        EditText productDescriptionText = findViewById(R.id.add_product_description);
        productDescriptionText.setText(description);
        EditText productPostcodeText = findViewById(R.id.add_product_postcode);
        productPostcodeText.setText(postcode);
    }

    private void categoryDropdown(Integer categoryID){
        Spinner spinner = findViewById(R.id.category_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(categoryID - 1);
    }

    private void conditionDropdown(String condition){
        Spinner spinner = findViewById(R.id.condition_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.condition, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(condition);
        spinner.setSelection(spinnerPosition);
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

    private void confirmListener(){
        ImageView confirmCheck = findViewById(R.id.tick);
        confirmCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the inputs from user
                EditText productNameText = findViewById(R.id.add_product_name);
                String productName = productNameText.getText().toString();
                EditText productDescriptionText = findViewById(R.id.add_product_description);
                String productDescription = productDescriptionText.getText().toString();
                Spinner categoryDropdown = findViewById(R.id.category_dropdown);
                Integer category = categoryDropdown.getSelectedItemPosition() + 1;
                Spinner conditionDropdown = findViewById(R.id.condition_dropdown);
                String condition = conditionDropdown.getSelectedItem().toString().toLowerCase();
                EditText productPostcodeText = findViewById(R.id.add_product_postcode);
                String productPostcode = productPostcodeText.getText().toString();

                //Checks if user inputs are all valid before uploading to backend
                if(checkUserInput(productNameText,productName,productPostcodeText,productPostcode)){
                    ArrayList<String> media = new ArrayList<>();
                    try {
                        ArrayList<String> dataURIList;
                        dataURIList = convertToDataURI(adapter.uploadedImages);
                        media.addAll(dataURIList);
                        BackendController.modifyListing(productID,productName,productDescription,"UK","Bristol",productPostcode,category,condition, media, ModifyProduct.this);
                        Toast toast = Toast.makeText(getApplicationContext(), "Modified Successfully!", Toast.LENGTH_LONG);
                        toast.show();
                        onBackPressed();
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Failed to modify the product", Toast.LENGTH_LONG);
                        toast.show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //convert the pictures uploaded by user to DataURI
    private ArrayList<String> convertToDataURI(SortedList<Uri> uriList) throws FileNotFoundException {
        ArrayList<String> dataURIList = new ArrayList<>();
        for(int i = 0; i < uriList.size(); i++){
            String dataURI = DataURIHelper.TranslateToDataURI(getApplicationContext(),uriList.get(i));
            dataURIList.add(dataURI);
        }
        return dataURIList;
    }

    // check if user inputs are all valid
    private boolean checkUserInput(EditText productNameText, String productName,EditText productPostcodeText, String productPostcode){
        if(!checkProductImages()) return false;
        else if(!checkProductName(productNameText,productName)) return false;
        else if(!checkProductPostcode(productPostcodeText,productPostcode)) return false;
        else return true;
    }

    // make sure the user has uploaded at least 1 picture
    private boolean checkProductImages(){
        if(adapter.uploadedImages.size() == 0){
            Toast toast = Toast.makeText(getApplicationContext(),"Please add a product image", Toast.LENGTH_LONG);
            toast.show();
            Button button = findViewById(R.id.add_image);
            button.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            return false;
        }else return true;
    }

    private boolean checkProductName(EditText productNameText, String productName){
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if(productName.length() == 0){
            Toast toast = Toast.makeText(getApplicationContext(),"Please input a product name", Toast.LENGTH_LONG);
            toast.show();
            productNameText.startAnimation(animShake);
            return false;
        }else if (!productName.matches("[a-zA-Z0-9.? ]*")){
            Toast toast = Toast.makeText(getApplicationContext(),"Please input a valid product name", Toast.LENGTH_LONG);
            toast.show();
            productNameText.startAnimation(animShake);
            return false;
        }
        return true;
    }

    private boolean checkProductPostcode(EditText productPostcodeText, String productPostcode){
        if (productPostcode.contains(" ")) {
            productPostcode = productPostcode.replace(" ", "");
            productPostcodeText.setText(productPostcode);
        }
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (productPostcode.length() > 7 || productPostcode.length() < 5) {
            Toast postcodeWarning = Toast.makeText(getApplicationContext(), "Please ensure you type your postcode in the correct format", Toast.LENGTH_LONG);
            postcodeWarning.show();
            productPostcodeText.startAnimation(animShake);
            return false;
        } else if (!Character.isAlphabetic(productPostcode.charAt(0)) ||
                !Character.isAlphabetic(productPostcode.charAt(productPostcode.length()-1)) ||
                !Character.isAlphabetic(productPostcode.charAt(productPostcode.length()-2))) {
            Toast postcodeWarning = Toast.makeText(getApplicationContext(), "Please ensure you type your postcode in the correct format", Toast.LENGTH_LONG);
            postcodeWarning.show();
            productPostcodeText.startAnimation(animShake);
            return false;
        } else {
            return true;
        }
    }

    private void addImageListener(){
        Button add_image = findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment add_image_popup = new addPhotoDialog();
                add_image_popup.show(getSupportFragmentManager(), "add_image_popup");
            }
        });
    }

    @Override
    public void onDialogActionClick(DialogFragment dialog, String action) {
        if(action == CAMERA){
            photoUri = CameraHelper.takePicture(getApplicationContext(),cameraActivityResultLauncher);
        }else if(action == GALLERY){
            galleryActivityResultLauncher.launch("image/*");
        }
    }

    @Override
    public void onBackendResult(boolean success, String message) {
        System.out.println("modify listing " + message);
    }
}
