package com.example.ar_reshare;

import static android.Manifest.permission_group.CAMERA;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity{
    private View addPhotoWindow;
    private ActivityResultLauncher cameraLauncher;
    private ActivityResultLauncher galleryLauncher;
    private File profilePicture = null;
    private Uri profilePictureURI = null;
    private final int GREEN_BACKGROUND_COLOR = Color.parseColor("#198235");
    private final int RED_COLOUR = Color.parseColor("#ab2a1f");
    private final int BLUE_BACKGROUND_COLOUR = Color.parseColor("#4C62DC");

    public void changePassword(String password, String newPassword){
        Map<String, String> changes = new HashMap<>();
        changes.put("password", password);
        changes.put("newPassword", newPassword);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        System.out.println("Your passward changed correctly");
                    }else{
                        System.out.println("Error");
                    }
                }
            });
        } catch (JSONException e){
    }}

    public void changeNickname(String name){
        Map<String, String> changes = new HashMap<>();
        changes.put("name", name);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        System.out.println("Your nickname have changed");
                    }else{
                        System.out.println("Error");
                    }
                }
            });
        } catch (JSONException e){
        }}

    public void changeDOB(String dob){
        Map<String, String> changes = new HashMap<>();
        changes.put("dob", dob);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        System.out.println("Your date of birth changed correctly");
                    }else{
                        System.out.println("Error");
                    }
                }
            });
        } catch (JSONException e){
        }}


    public void changeEmail(String Email, String password){
        Map<String, String> changes = new HashMap<>();
        changes.put("Email", Email);
        changes.put("password", password);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        System.out.println("Your Email address changed correctly");
                    }else{
                        System.out.println("Error");
                    }
                }
            });
        } catch (JSONException e){
        }}

    public void changeHomeAddress(String Address){
        Map<String, String> changes = new HashMap<>();
        changes.put("Address", Address);
        try {
            BackendController.modifyAccount(getApplicationContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if(success){
                        System.out.println("Your home address changed correctly");
                    }else{
                        System.out.println("Error");
                    }
                }
            });
        } catch (JSONException e){
        }}

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

        Button nicknameButton = (Button) findViewById(R.id.name);
        nicknameButton.setOnClickListener(new View.OnClickListener() {
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
                        changeNickname(c);
                        Toast.makeText(v.getContext(), "New name:" + a +"_"+ b, Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder dialog3 = new AlertDialog.Builder(v.getContext());
                dialog3.setIcon(R.drawable.settings_birthday);
                dialog3.setTitle("Please insert your new birthday");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.birthday_dialog, null);
                dialog3.setView(view);

                final EditText year = (EditText) view.findViewById(R.id.Year);
                final EditText month = (EditText) view.findViewById(R.id.Month);
                final EditText day = (EditText) view.findViewById(R.id.Day);

                dialog3.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = year.getText().toString().trim();
                        String b = month.getText().toString().trim();
                        String c = day.getText().toString().trim();

                        String d = a + "-"+  b + "-" + c;
                        changeDOB(d);
                        Toast.makeText(v.getContext(), "New Birthday:" + a +"_"+ b + "_" + c, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog3.show();
            }
        });

        Button pwdButton = (Button) findViewById(R.id.pwd);
        pwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog4 = new AlertDialog.Builder(v.getContext());
                dialog4.setIcon(R.drawable.setting_pwd);
                dialog4.setTitle("Please insert your new Password");

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
                        Toast.makeText(v.getContext(), "Your password has been changed", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(v.getContext(), "New Email Address:" + a, Toast.LENGTH_SHORT).show();
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

       Button homeaddressButton = (Button) findViewById(R.id.homeaddress);
       homeaddressButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                AlertDialog.Builder dialog6 = new AlertDialog.Builder(v.getContext());
                dialog6.setIcon(R.drawable.setting_home);
                dialog6.setTitle("Please insert your new Postcode");

                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.homeaddress_dialog, null);
                dialog6.setView(view);

               final EditText postcode = (EditText) view.findViewById(R.id.setting_Postcode);

               dialog6.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String a = postcode.getText().toString().trim();
                       changeHomeAddress(a);
                       Toast.makeText(v.getContext(), "New Postcode:" + a, Toast.LENGTH_SHORT).show();
                   }
               });
               dialog6.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
                dialog6.show();
           }
       });

        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog7 = new AlertDialog.Builder(v.getContext());
                dialog7.setTitle("Warning").setMessage("Are you sure you want to Log out");
                dialog7.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, LoginSignupActivity.class);
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
                dialog8.setTitle("Warning").setMessage("Are you sure you want to Delete your Account");
                dialog8.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
            BackendController.modifyAccount(SettingActivity.this, changes, new BackendController.BackendCallback() {
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
}