package com.example.ar_reshare;

import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StorageActivity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    //ArrayList<Uri> uploadUriList = new ArrayList<>();
    public ArrayList<Uri> downloadUriList = new ArrayList(); //the uri list after successfully uploading to cloud
    private uploadingFinishedListener onFinishedCallback;
    private int totalCount;
    public StorageActivity(uploadingFinishedListener onFinishedCallback){
        this.onFinishedCallback = onFinishedCallback;
    }

    interface uploadingFinishedListener{
        void notifyUploadingFinished(ArrayList<Uri> downloadUriList );
    }

    public void uploadImages(SortedList<Uri> uriList){
        this.totalCount = uriList.size();
        for (int i = 0; i < totalCount; i++) {
            Uri currentUri = uriList.get(i);
            StorageReference imageRef = storageRef.child("images/" + currentUri.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(currentUri);
            uploadingImage(uploadTask,imageRef);
        }
    }

    private void uploadingImage(UploadTask uploadTask, StorageReference imageRef){
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("failed to upload");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                //generates download uris if uploaded successfully
                produceDownloadUri(uploadTask,imageRef);
            }
        });
    }

    private void produceDownloadUri(UploadTask uploadTask, StorageReference imageRef){
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    downloadUriList.add(downloadUri);
                    if(downloadUriList.size() == totalCount){
                        onFinishedCallback.notifyUploadingFinished(downloadUriList);
                    }

                    System.out.println("where is url llllllll" + downloadUri);
                } else {
                    // Handle failures
                    System.out.println("failed to download");
                }
            }
        });
    }
}
