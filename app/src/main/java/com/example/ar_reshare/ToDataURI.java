package com.example.ar_reshare;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;

public class ToDataURI {
    public static String TranslateToDataURI(Context context, Uri uri) throws FileNotFoundException {

        InputStream iStream = context.getContentResolver().openInputStream(uri);
        byte[] inputData = getBytes(iStream);
        String base64str = Base64.getEncoder().encodeToString(inputData);
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String contentType = mime.getExtensionFromMimeType(cR.getType(uri));
        // cretate "data URI"
        StringBuilder sb = new StringBuilder();
        sb.append("data:");
        sb.append("image/");
        sb.append(contentType);
        sb.append(";base64,");
        sb.append(base64str);
        return sb.toString();
    }

    private static byte[] getBytes(InputStream iStream) {
        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            int len;
            while ((len = iStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }


}
