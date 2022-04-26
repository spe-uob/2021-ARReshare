package com.example.ar_reshare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHelper {
    private static File storageDir;
    private static File photoFile;

    //Starts the camera intent and returns the uri of the image file created
    public static Uri takePicture(Context context, ActivityResultLauncher<Intent> cameraActivityResultLauncher){
        storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(context,
                    "com.example.ar_reshare.fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        cameraActivityResultLauncher.launch(intent);
        return photoUri;
    }

    //creates a local photo path to store the pics taken by user
    private static File createImageFile() throws IOException {
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

    //Deletes the image stored if not used
    public static void deleteImageFile(){
        if(photoFile != null){
            photoFile.delete();//delete the file if failed
        }
    }
}

