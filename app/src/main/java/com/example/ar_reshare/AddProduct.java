package com.example.ar_reshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProduct extends AppCompatActivity implements addPhotoDialog.NoticeDialogListener{

    private ArrayList<Uri> uploadedImages = new ArrayList<>();
    UploadImageAdapter adapter;
    private final String CAMERA = "camera";
    private final String GALLERY = "gallery";
    ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    ActivityResultLauncher<String> galleryActivityResultLauncher;
    File storageDir;
    Uri photoUri;
    ArrayList<Uri> photoUris;
    String currentPhotoPath;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        uploadedImageView();
        categoryDropdown();
        conditionDropdown();
        addImageListener();

        //Camera intent launcher, add the picture to the product pics list once finished
        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            System.out.println("helloooo");
                            adapter.addItem(photoUri);
                        }else{
                            photoFile.delete();//delete the file if failed
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

        storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        photoUris = new ArrayList<>(); // store all the pics uris
        confirmListener();
        returnListener();
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


    // used a recycler view to display the images chosen by users
    private void uploadedImageView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        RecyclerView uploaded_image_list = findViewById(R.id.uploaded_image_list);
        uploaded_image_list.setLayoutManager(layoutManager);
        adapter = new UploadImageAdapter(uploadedImages);
        uploaded_image_list.setAdapter(adapter);
    }

    private void categoryDropdown(){
        Spinner spinner = findViewById(R.id.category_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void conditionDropdown(){
        Spinner spinner = findViewById(R.id.condition_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.condition, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

    // generates a pop up window showing "added successfully"
    private void confirmListener(){
        ImageView confirmCheck = findViewById(R.id.tick);
        confirmCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Added Successfully!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    @Override
    public void onDialogActionClick(DialogFragment dialog, String action) {
        if(action == CAMERA){
            takePicture();
        }else if(action == GALLERY){
            accessGallery();
        }
    }

    private void accessGallery(){
        galleryActivityResultLauncher.launch("image/*");
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.example.ar_reshare.fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        cameraActivityResultLauncher.launch(intent);
    }

    //creates a local photo path to store the pics taken by user
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}