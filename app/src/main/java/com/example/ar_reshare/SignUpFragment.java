package com.example.ar_reshare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {

    private final int DIALOG_TIME = 3000; // milliseconds
    private final int GREEN_COLOUR = Color.parseColor("#32a852");
    private final int GREEN_BACKGROUND_COLOR = Color.parseColor("#198235");
    private final int RED_COLOUR = Color.parseColor("#ab2a1f");
    private final int DEFAULT_TEXT_COLOUR = Color.parseColor("#363636");
    private final int BLUE_BACKGROUND_COLOUR = Color.parseColor("#4C62DC");
    private final int CLICKABLE_COLOUR = Color.parseColor("#4C62DC");
    private final int NOT_CLICKABLE_COLOUR = Color.parseColor("#7080db");

    private final List<Character> SPECIAL_CHARACTERS = Arrays.asList('@', '!', '?', '%', '+', '-', '\\', '/', '\'', '$', '#', '^', ':', ';', '(', ')', '[', ']', '{', '}', '~', '_', '.');

    private final long MINIMUM_AGE_REQUIRED = 18L;
    private final int RESULT_OK = -1;

    private File profilePicture = null;
    private Uri profilePictureURI = null;

    private View addPhotoWindow;
    private ActivityResultLauncher cameraLauncher;
    private ActivityResultLauncher galleryLauncher;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EditText dobText = view.findViewById(R.id.signUpDateOfBirth);
        dobText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog.OnDateSetListener dateListener = new dateChangedListener();
                    Calendar now = Calendar.getInstance();
                    new DatePickerDialog(getContext(), dateListener, now
                            .get(Calendar.YEAR), now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        dobText.addTextChangedListener(new textChangedListener(dobText));

        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText confirmPasswordText = getView().findViewById(R.id.signUpConfirmPassword);
        TextWatcher passwordListener = new passwordChangedListener();
        passwordText.addTextChangedListener(passwordListener);
        confirmPasswordText.addTextChangedListener(passwordListener);

        EditText emailText = getView().findViewById(R.id.signUpEmail);
        TextWatcher emailListener = new textChangedListener(emailText);
        emailText.addTextChangedListener(emailListener);

        EditText postcodeText = getView().findViewById(R.id.signUpPostCode);
        TextWatcher postcodeListener = new textChangedListener(postcodeText);
        postcodeText.addTextChangedListener(postcodeListener);

        Button signUpButton = getView().findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new signUpButtonListener());
    }

    private void changeDateField(Date date) {
        EditText dobText = getView().findViewById(R.id.signUpDateOfBirth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dobText.setText(dateFormat.format(date));
        verifyDob();
    }

    // Verifies if the user meets the minimum age to register
    private boolean verifyDob() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        EditText dobText = getView().findViewById(R.id.signUpDateOfBirth);
        try {
            Date dob = dateFormat.parse(dobText.getText().toString());
            Date now = Calendar.getInstance().getTime();
            long years = TimeUnit.DAYS.convert(now.getTime() - dob.getTime(), TimeUnit.MILLISECONDS);
            if (years >= MINIMUM_AGE_REQUIRED) {
                dobText.setTextColor(DEFAULT_TEXT_COLOUR);
                return true;
            } else {
                dobText.setTextColor(RED_COLOUR);
                Toast dateWarning = Toast.makeText(getContext(), "You must be at least 18 year old to use this app", Toast.LENGTH_LONG);
                dateWarning.show();
                return false;
            }
        } catch (ParseException e) {
            dobText.setTextColor(RED_COLOUR);
            Toast dateWarning = Toast.makeText(getContext(), "Date of birth must be in the form dd/mm/yyyy", Toast.LENGTH_LONG);
            dateWarning.show();
            return false;
        }
    }

    // Verifies if passwords match
    private boolean verifyPasswordsIdentical() {
        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText confirmPasswordText = getView().findViewById(R.id.signUpConfirmPassword);
        TextView passwordComment = getView().findViewById(R.id.signUpPasswordComment);
        if (passwordText.getText().toString().isEmpty() && confirmPasswordText.getText().toString().isEmpty()) {
            passwordComment.setVisibility(View.VISIBLE);
            passwordComment.setText("Password is required!");
            passwordComment.setTextColor(RED_COLOUR);
            return false;
        }
        else if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
            passwordComment.setVisibility(View.VISIBLE);
            passwordComment.setText("Passwords do not match!");
            passwordComment.setTextColor(RED_COLOUR);
            return false;
        } else {
            passwordComment.setVisibility(View.VISIBLE);
            passwordComment.setText("Passwords match ✓");
            passwordComment.setTextColor(GREEN_COLOUR);
            return true;
        }
    }

    private boolean checkPasswordStrength() {
        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        TextView passwordComment = getView().findViewById(R.id.signUpPasswordComment);
        String password = passwordText.getText().toString();
        if (password.length() < 8) {
            passwordComment.setVisibility(View.VISIBLE);
            passwordComment.setText("Password must be at least 8 characters long!");
            passwordComment.setTextColor(RED_COLOUR);
            return false;
        } else {
            boolean containsUppercase = password.matches(".*[a-z].*");
            boolean containsLowercase = password.matches(".*[A-Z].*");
            if (!containsLowercase || !containsUppercase) {
                passwordComment.setVisibility(View.VISIBLE);
                passwordComment.setText("Password must have both lower and upper case characters!");
                passwordComment.setTextColor(RED_COLOUR);
                return false;
            }

            boolean containsNumber = password.matches(".*[0-9].*");
            if (!containsNumber) {
                passwordComment.setVisibility(View.VISIBLE);
                passwordComment.setText("Password must contain a number!");
                passwordComment.setTextColor(RED_COLOUR);
                return false;
            }

            boolean specialCharacters = SPECIAL_CHARACTERS.stream()
                    .map(character -> password.contains(character.toString()))
                    .reduce(false, (x, acc) -> acc || x);
            if (!specialCharacters) {
                passwordComment.setVisibility(View.VISIBLE);
                passwordComment.setText("Password must have special characters!");
                passwordComment.setTextColor(RED_COLOUR);
                return false;
            }
        }
        return true;
    }

    private boolean verifyEmail() {
        EditText emailText = getView().findViewById(R.id.signUpEmail);
        if (emailText.getText().toString().contains("@")) {
            emailText.setTextColor(DEFAULT_TEXT_COLOUR);
            return true;
        } else {
            emailText.setTextColor(RED_COLOUR);
            Toast dateWarning = Toast.makeText(getContext(), "Please make sure you provide a valid email address ", Toast.LENGTH_LONG);
            dateWarning.show();
            return false;
        }
    }

    private boolean verifyPostcode() {
        EditText postcodeText = getView().findViewById(R.id.signUpPostCode);
        String postcode = postcodeText.getText().toString();
        if (postcode.contains(" ")) {
            postcode = postcode.replace(" ", "");
            postcodeText.setText(postcode);
        }
        if (postcode.length() > 7 || postcode.length() < 5) {
            postcodeText.setTextColor(RED_COLOUR);
            Toast postcodeWarning = Toast.makeText(getContext(), "Please ensure you type your postcode in the correct format", Toast.LENGTH_LONG);
            postcodeWarning.show();
            return false;
        } else if (!Character.isAlphabetic(postcode.charAt(0)) ||
                !Character.isAlphabetic(postcode.charAt(postcode.length()-1)) ||
                !Character.isAlphabetic(postcode.charAt(postcode.length()-2))) {
            postcodeText.setTextColor(RED_COLOUR);
            Toast postcodeWarning = Toast.makeText(getContext(), "Please ensure you type your postcode in the correct format", Toast.LENGTH_LONG);
            postcodeWarning.show();
            return false;
        } else {
            postcodeText.setTextColor(DEFAULT_TEXT_COLOUR);
            return true;
        }
    }

    // Verifies all user inputs
    private boolean verifyAllInputs() {
        if (!verifyEmail()) return false;
        if (!verifyPasswordsIdentical()) return false;
        if (!checkPasswordStrength()) return false;
        if (!verifyDob()) return false;
        if (!verifyPostcode()) return false;
        return true;
    }

    private void registerUser() {
        // Signal to the user the registration is taking place
        Button signUpButton = getView().findViewById(R.id.signUpButton);
        signUpButton.setText("Signing Up...");

        EditText firstNameText = getView().findViewById(R.id.signUpFirstName);
        EditText lastNameText = getView().findViewById(R.id.signUpLastName);
        EditText emailText = getView().findViewById(R.id.signUpEmail);
        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText dobText = getView().findViewById(R.id.signUpDateOfBirth);
        EditText postcodeText = getView().findViewById(R.id.signUpDateOfBirth);

        String name = firstNameText.getText().toString() + " " + lastNameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String dob = dobText.getText().toString();
        String postcode = postcodeText.getText().toString();

        BackendController.registerAccount(name, email, password, dob, postcode, new BackendController.BackendCallback() {
            @Override
            public void onBackendResult(boolean success, String message) {
                if (success) {
                    BackendController.loginAccount(email, password, new BackendController.BackendCallback() {
                        @Override
                        public void onBackendResult(boolean success, String message) {
                            if (success) {
                                // Add this AR-Reshare account to account manager
                                Pair<byte[], byte[]> encryptedPair = Crypto.encrypt(password);
                                AuthenticationService.addAccount(getContext(), email, encryptedPair.first, encryptedPair.second);
                                displaySuccess();
                            }
                            else {
                                signUpButton.setEnabled(true);
                                displayFailure();
                            }
                        }
                    });
                } else {
                    signUpButton.setEnabled(true);
                    displayFailure();
                }
            }
        });
    }

    private void displaySuccess() {
        AlertDialog dialog = createDialog("Registration Successful!",
                "Your account has been successfully registered");
        dialog.setOnShowListener(dialog1 -> new CountDownTimer(DIALOG_TIME, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                if (((AlertDialog) dialog1).isShowing()) {
                    dialog1.dismiss();
                    askForProfilePicture();
                }
            }
        }.start());
        dialog.show();
    }

    private AlertDialog createDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void displayFailure() {
        Toast unsuccessful = Toast.makeText(getContext(), "Failed to create an account!", Toast.LENGTH_LONG);
        unsuccessful.show();

        // Change button text to default
        Button signUpButton = getView().findViewById(R.id.signUpButton);
        signUpButton.setText("Sign Up");
    }

    // Inflates a popup window asking the user to upload a profile picture
    private void askForProfilePicture() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addPhotoWindow = inflater.inflate(R.layout.add_user_photo, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        // Get user's name
        EditText firstNameText = getView().findViewById(R.id.signUpFirstName);
        EditText lastNameText = getView().findViewById(R.id.signUpLastName);
        String name = firstNameText.getText().toString() + " " + lastNameText.getText().toString();

        // Allows to tap outside the popup to dismiss it
        boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(addPhotoWindow, width, height, focusable);

        TextView nameText = addPhotoWindow.findViewById(R.id.signupPictureName);
        nameText.setText(name);

        Button cameraButton = addPhotoWindow.findViewById(R.id.signupPictureCamera);
        Button galleryButton = addPhotoWindow.findViewById(R.id.signupPictureGallery);
        Button skipButton = addPhotoWindow.findViewById(R.id.signupPictureCancel);
        cameraButton.setOnClickListener(v -> takePhoto());
        galleryButton.setOnClickListener(v -> chooseFromGallery());
        skipButton.setOnClickListener(v -> proceed());

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
                    FileProvider.getUriForFile(getContext(), "com.example.ar_reshare.fileprovider", profilePicture);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, profilePictureURI);
        }

        cameraLauncher.launch(intent);
    }

    // Creates a local photo path to store the picture taken by user
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        Button skipButton = addPhotoWindow.findViewById(R.id.signupPictureCancel);
        cameraButton.setText("Confirm ✓");
        cameraButton.setBackgroundColor(GREEN_BACKGROUND_COLOR);
        galleryButton.setText("Choose a new photo");
        galleryButton.setBackgroundColor(RED_COLOUR);
        skipButton.setText("Cancel and Skip");

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
    }

    private void uploadProfilePicture() {
        try {
            Button button = addPhotoWindow.findViewById(R.id.signupPictureCamera);
            button.setText("Uploading...");
            String sourceURI = DataURIHelper.TranslateToDataURI(getContext(), profilePictureURI);
            Map<String, String> changes = new HashMap<>();
            changes.put("picture", sourceURI);
            BackendController.modifyAccount(getContext(), changes, new BackendController.BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if (success) {
                        AlertDialog successful = createDialog("Success!", "Your profile picture has been uploaded.");
                        successful.setOnShowListener(dialog -> {
                            new CountDownTimer(DIALOG_TIME, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }
                            @Override
                            public void onFinish() {
                                if (((AlertDialog) dialog).isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                            }.start();
                        });
                        successful.show();
                        proceed();
                    }
                    else {
                        defaultButtons();
                        Toast unsuccessful = Toast.makeText(getContext(), "Failed to upload the picture", Toast.LENGTH_LONG);
                        unsuccessful.show();
                    }
                }
            });
        } catch (Exception e) {
            defaultButtons();
            Toast unsuccessful = Toast.makeText(getContext(), "Failed to upload the picture", Toast.LENGTH_LONG);
            unsuccessful.show();
        }
    }

    private void proceed() {
        Intent intent = new Intent(getContext(), SwipeActivity.class);
        startActivity(intent);
    }

    public class textChangedListener implements TextWatcher {
        private EditText watchedText;

        textChangedListener(EditText view) {
            this.watchedText = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            watchedText.setTextColor(DEFAULT_TEXT_COLOUR);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class dateChangedListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Date chosenDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
            changeDateField(chosenDate);
        }
    }

    private class passwordChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (verifyPasswordsIdentical()) {
                checkPasswordStrength();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class signUpButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Disable button while request is sent
            getView().findViewById(R.id.signUpButton).setEnabled(false);
            if (verifyAllInputs()) {
                registerUser();
            } else {
                // Enable the button if verification failed
                getView().findViewById(R.id.signUpButton).setEnabled(true);
            }
        }
    }
}