package com.example.ar_reshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageHelper {

    // Callback interface
    public interface ImageDownloadCallback {
        void onImageDownloaded(boolean success, Bitmap image);
    }

    // Given an image URL, download the image and convert to Bitmap
    public static void downloadImage(String imageURL, ImageDownloadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("SENDING");
                    URL urlConnection = new URL(imageURL);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap image = BitmapFactory.decodeStream(input);
                    System.out.println("RESULT IS BACK");
                    callback.onImageDownloaded(true, image);
                } catch (Exception e) {
                    callback.onImageDownloaded(false, null);
                }
            }
        }).start();
    }
}
