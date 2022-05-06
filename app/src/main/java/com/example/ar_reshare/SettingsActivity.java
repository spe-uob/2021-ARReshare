package com.example.ar_reshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity{
    private View addPhotoWindow;
    private ActivityResultLauncher cameraLauncher;
    private ActivityResultLauncher galleryLauncher;
    private File profilePicture = null;
    private Uri profilePictureURI = null;
    private final int GREEN_BACKGROUND_COLOR = Color.parseColor("#198235");
    private final int RED_COLOUR = Color.parseColor("#ab2a1f");
    private final int BLUE_BACKGROUND_COLOUR = Color.parseColor("#4C62DC");
    private static final long MINIMUM_AGE_REQUIRED = 18;

    public void changePassword(String password, String newPassword){
        Map<String, String> changes = new HashMap<>();
        changes.put("password", password);
        changes.put("newPassword", newPassword);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        Toast.makeText(getApplicationContext(), "Your password has been changed successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
    }}

    public void changeName(String name){
        Map<String, String> changes = new HashMap<>();
        changes.put("name", name);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        Toast.makeText(getApplicationContext(), "Your name has been changed successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to change name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Failed to change name", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeEmail(String email, String password){
        Map<String, String> changes = new HashMap<>();
        changes.put("email", email);
        changes.put("password", password);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        Toast.makeText(getApplicationContext(), "Your email has been changed successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to change email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Failed to change email", Toast.LENGTH_SHORT).show();
        }}


    public void deleteAccount(String password){
        BackendController.closeAccount(password, new BackendController.BackendCallback() {
            @Override
            public void onBackendResult(boolean success, String message) {
                if (success) {
                    Toast.makeText(getApplicationContext(), "Account successfully deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, LoginSignupActivity.class);
                    AuthenticationService.removeAccounts(getApplicationContext());
                    startActivity((intent));
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    ImageView profilePictureView = addPhotoWindow.findViewById(R.id.signupProfilePicture);
                    profilePictureView.setImageURI(profilePictureURI);
                    onProfilePictureChanged();
                } else {
                    profilePicture.delete();
                }
            }
        });

        // Gallery intent launcher, allowing users to select multiple pictures at a time
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profilePictureURI = result;
                ImageView profilePictureView = addPhotoWindow.findViewById(R.id.signupProfilePicture);
                profilePictureView.setImageURI(profilePictureURI);
                onProfilePictureChanged();
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Button nameButton = (Button) findViewById(R.id.name);
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(v.getContext());
                dialog2.setIcon(R.drawable.setting_name);
                dialog2.setTitle("Please insert your new name");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.fullname_dialog, null);
                dialog2.setView(view);

                final EditText firstName = (EditText) view.findViewById(R.id.Firstname);
                final EditText lastName = (EditText) view.findViewById(R.id.Lastname);

                dialog2.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = firstName.getText().toString().trim();
                        String b = lastName.getText().toString().trim();
                        String c = a + " " + b;
                        changeName(c);
                    }
                });
                dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog2.show();
            }
        });

        Button birthdayButton = (Button) findViewById(R.id.birthday);
        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateListener = new dateChangedListener();
                Calendar now = Calendar.getInstance();
                new DatePickerDialog(SettingsActivity.this, dateListener, now
                        .get(Calendar.YEAR), now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button pwdButton = (Button) findViewById(R.id.pwd);
        pwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog4 = new AlertDialog.Builder(v.getContext());
                dialog4.setIcon(R.drawable.setting_pwd);
                dialog4.setTitle("Please insert your new password");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.pwd_dialog, null);
                dialog4.setView(view);

                final EditText oldpwd = (EditText) view.findViewById(R.id.oldpwd);
                final EditText newpwd = (EditText) view.findViewById(R.id.newpwd);

                dialog4.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = oldpwd.getText().toString().trim();
                        String b = newpwd.getText().toString().trim();

                        changePassword(a, b);
                    }
                });
                dialog4.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog4.show();
            }
        });

        Button emailButton =(Button) findViewById(R.id.email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog5 = new AlertDialog.Builder(v.getContext());
                dialog5.setIcon(R.drawable.setting_email);
                dialog5.setTitle("Please insert your new email address");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.email_dialog, null);
                dialog5.setView(view);

                final EditText email = (EditText) view.findViewById(R.id.email);
                final EditText pwdE = (EditText) view.findViewById(R.id.pwdEmail);

                dialog5.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = email.getText().toString().trim();
                        String b = pwdE.getText().toString().trim();

                        changeEmail(a, b);
                    }
                });
                dialog5.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog5.show();
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog7 = new AlertDialog.Builder(v.getContext());
                dialog7.setTitle("Warning").setMessage("Are you sure you want to log out?");
                dialog7.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingsActivity.this, LoginSignupActivity.class);
                        AuthenticationService.removeAccounts(getApplicationContext());
                        startActivity((intent));
                    }
                });
                dialog7.setNegativeButton("Cancel", null);
                dialog7.show() ;
            }
        });

        Button deleteButton = (Button) findViewById(R.id.detele);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog8 = new AlertDialog.Builder(v.getContext());
                dialog8.setTitle("Warning").setMessage("Are you sure you want to delete your account?");
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.delete_account_dialog, null);
                dialog8.setView(view);

                final EditText password = (EditText) view.findViewById(R.id.delete_account_password);
                dialog8.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount(password.getText().toString());
                    }
                });
                dialog8.setNegativeButton("Cancel", null);
                dialog8.show();
            }
        });

        Button profilePicButton = findViewById(R.id.profile_picture);
        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForProfilePicture();
            }
        });
    }

    // Inflates a popup window asking the user to upload a profile picture
    private void askForProfilePicture() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        addPhotoWindow = inflater.inflate(R.layout.add_user_photo, null);
        addPhotoWindow.findViewById(R.id.profile_pic_layout).setBackgroundResource(R.drawable.border);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        // Allows to tap outside the popup to dismiss it
        boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(addPhotoWindow, width, height, focusable);
        addPhotoWindow.findViewById(R.id.signupPictureName).setVisibility(View.INVISIBLE);
        Button cameraButton = addPhotoWindow.findViewById(R.id.signupPictureCamera);
        Button galleryButton = addPhotoWindow.findViewById(R.id.signupPictureGallery);
        Button skip = addPhotoWindow.findViewById(R.id.signupPictureCancel);
        cameraButton.setOnClickListener(v -> takePhoto());
        galleryButton.setOnClickListener(v -> chooseFromGallery());
        skip.setOnClickListener(v -> onBackPressed());
        popupWindow.showAtLocation(addPhotoWindow, Gravity.CENTER, 0, 0);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            profilePicture = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the file
            ex.printStackTrace();
        }
        // Continue only if the file was successfully created
        if (profilePicture != null) {
            profilePictureURI =
                    FileProvider.getUriForFile(this, "com.example.ar_reshare.fileprovider", profilePicture);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, profilePictureURI);
        }
        cameraLauncher.launch(intent);
    }

    // Creates a local photo path to store the picture taken by user
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    // Allows the user to choose a photo from the photo gallery
    private void chooseFromGallery() {
        galleryLauncher.launch("image/*");
    }

    // Changes button text and colours when a picture has been chosen
    private void onProfilePictureChanged() {
        Button cameraButton = addPhotoWindow.findViewById(R.id.signupPictureCamera);
        Button galleryButton = addPhotoWindow.findViewById(R.id.signupPictureGallery);

        cameraButton.setText("Confirm ✓");
        cameraButton.setBackgroundColor(GREEN_BACKGROUND_COLOR);
        galleryButton.setText("Choose a new photo");
        galleryButton.setBackgroundColor(RED_COLOUR);

        galleryButton.setOnClickListener(v -> defaultButtons());
        cameraButton.setOnClickListener(v -> uploadProfilePicture());
    }

    // Resets button text and colours to default
    private void defaultButtons() {
        Button cameraButton = addPhotoWindow.findViewById(R.id.signupPictureCamera);
        Button galleryButton = addPhotoWindow.findViewById(R.id.signupPictureGallery);
        Button skipButton = addPhotoWindow.findViewById(R.id.signupPictureCancel);
        cameraButton.setText(getResources().getText(R.string.signupProfileCameraText));
        cameraButton.setBackgroundColor(BLUE_BACKGROUND_COLOUR);
        galleryButton.setText(getResources().getText(R.string.signupProfileGalleryText));
        galleryButton.setBackgroundColor(BLUE_BACKGROUND_COLOUR);
        skipButton.setText(getResources().getText(R.string.signupProfileCancelText));
        galleryButton.setOnClickListener(v -> chooseFromGallery());
        cameraButton.setOnClickListener(v -> takePhoto());
        skipButton.setOnClickListener(v -> onBackPressed());
    }

    private void uploadProfilePicture() {
        try {
            Button button = addPhotoWindow.findViewById(R.id.signupPictureCamera);
            button.setText("Uploading...");
            String sourceURI = DataURIHelper.TranslateToDataURI(this, profilePictureURI);
            Map<String, String> changes = new HashMap<>();
            changes.put("picture", sourceURI);
            BackendController.modifyAccount(SettingsActivity.this, changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if (success) {
                        onBackPressed();
                    }
                    else {
                        defaultButtons();
                        Toast unsuccessful = Toast.makeText(getApplicationContext(), "Failed to upload the picture", Toast.LENGTH_LONG);
                        unsuccessful.show();
                    }
                }
            });
        } catch (Exception e) {
            defaultButtons();
            Toast unsuccessful = Toast.makeText(getApplicationContext(), "Failed to upload the picture", Toast.LENGTH_LONG);
            unsuccessful.show();
        }
    }

    private void changeDateField(Date date) {
        boolean verified = verifyDob(date);
        if (verified) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, String> changes = new HashMap<>();
            changes.put("dob", dateFormat.format(date));
            try {
                BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                    @Override
                    public void onBackendResult(boolean success, String message) {
                        if (success) {
                            Toast.makeText(getApplicationContext(), "Your date of birth has been changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to change date of birth", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Failed to change date of birth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Verifies if the user meets the minimum age to register
    private boolean verifyDob(Date dob) {
        Date now = Calendar.getInstance().getTime();
        long years = TimeUnit.DAYS.convert(now.getTime() - dob.getTime(), TimeUnit.MILLISECONDS);
        if (years >= MINIMUM_AGE_REQUIRED) {
            return true;
        } else {
            Toast dateWarning = Toast.makeText(getApplicationContext(), "You must be at least 18 year old to use this app", Toast.LENGTH_LONG);
            dateWarning.show();
            return false;
        }
    }

    private class dateChangedListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Date chosenDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
            changeDateField(chosenDate);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}