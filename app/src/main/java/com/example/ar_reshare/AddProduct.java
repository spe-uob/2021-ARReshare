package com.example.ar_reshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

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
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProduct extends AppCompatActivity implements addPhotoDialog.NoticeDialogListener, BackendController.BackendCallback, StorageActivity.uploadingFinishedListener{

    private ArrayList<Uri> uploadedImages = new ArrayList<>();
    public ArrayList<Uri> downloadUriList = new ArrayList<>();
    private UploadImageAdapter adapter;
    private final String CAMERA = "camera";
    private final String GALLERY = "gallery";
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<String> galleryActivityResultLauncher;
    private File storageDir;
    private Uri photoUri;
    private File photoFile;

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
                uploadImagesToCloud(adapter.uploadedImages);
                Context context = getApplicationContext();
                CharSequence text = "Added Successfully!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    public void uploadImagesToCloud(SortedList<Uri> uploadImages){
        StorageActivity sa = new StorageActivity(AddProduct.this);
        sa.uploadImages(uploadImages);
        System.out.println(sa.downloadUriList);
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
        return image;
    }

    @Override
    public void onBackendResult(boolean success, String message) {
        System.out.println(message);
    }

    @Override
    public void notifyUploadingFinished(ArrayList<Uri> downloadUriList) {
        try {
            ArrayList<String> media = new ArrayList<>();
            media.add("123123");
            media.add("2313123");
            System.out.println(downloadUriList.get(0));
            BackendController.addProduct("hello","idk","hello","hello","hello",5,"used",media, AddProduct.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onBackPressed();
    }
}