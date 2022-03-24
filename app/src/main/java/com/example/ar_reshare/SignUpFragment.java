package com.example.ar_reshare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {

    private final int GREEN_COLOUR = Color.parseColor("#32a852");
    private final int RED_COLOUR = Color.parseColor("#ab2a1f");
    private final int DEFAULT_TEXT_COLOUR = Color.parseColor("#808080");
    private final int CLICKABLE_COLOUR = Color.parseColor("#4C62DC");
    private final int NOT_CLICKABLE_COLOUR = Color.parseColor("#7080db");

    private final long MINIMUM_AGE_REQUIRED = 18L;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dobText.setText(dateFormat.format(date));
        verifyDob();
    }

    // Verifies if the user meets the minimum age to register
    private boolean verifyDob() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
            postcode = postcode.replaceFirst(" ", "");
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
        if (!verifyDob()) return false;
        if (!verifyPostcode()) return false;
        return true;
    }

    // TODO: Send user registration to backend
    private boolean registerUser() {
        boolean successful = true;
        if (successful) {
            EditText emailText = getView().findViewById(R.id.signUpEmail);
            EditText passwordText = getView().findViewById(R.id.signUpPassword);
            Pair<byte[], byte[]> encryptedPair = Crypto.encrypt(passwordText.getText().toString());

            System.out.println("ORIGINAL BINARY");
            System.out.println(encryptedPair.second);
            System.out.println("STRINGIFIED BINARY");
            String password = Base64.getEncoder().encodeToString(encryptedPair.first);
            String iv = Base64.getEncoder().encodeToString(encryptedPair.second);
            System.out.println(iv);
            System.out.println("DESTRINGIFIED BINARY");
            System.out.println(Base64.getDecoder().decode(iv));

            LoginSignupActivity parent = (LoginSignupActivity) getActivity();
            AuthenticationService.addAccount(getContext(), emailText.getText().toString(), encryptedPair.first, encryptedPair.second);
            return true;
        } else {
            return false;
        }
    }

    private void displaySuccess() {
        AlertDialog.Builder successful = new AlertDialog.Builder(getContext());
        successful.setTitle("Registration Successful!");
        successful.setMessage("Your account has been successfully registered");
        AlertDialog dialog = successful.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new CountDownTimer(3000, 100) {
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
            }
        });
        dialog.show();
        Intent intent = new Intent(getContext(), ARActivity.class);
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
            verifyPasswordsIdentical();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class signUpButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (verifyAllInputs()) {
                registerUser();
            }
        }
    }
}