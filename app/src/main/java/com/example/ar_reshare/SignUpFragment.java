package com.example.ar_reshare;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {

    private final int GREEN_COLOUR = Color.parseColor("#32a852");
    private final int RED_COLOUR = Color.parseColor("#ab2a1f");
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
        dobText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText confirmPasswordText = getView().findViewById(R.id.signUpConfirmPassword);
        TextWatcher passwordListener = new passwordChangedListener();
        passwordText.addTextChangedListener(passwordListener);
        confirmPasswordText.addTextChangedListener(passwordListener);
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
                return true;
            } else {
                Toast dateWarning = Toast.makeText(getContext(), "You must be at least 18 year old to use this app", Toast.LENGTH_LONG);
                dateWarning.show();
                return false;
            }
        } catch (ParseException e) {
            Toast dateWarning = Toast.makeText(getContext(), "Date of birth must be in the form dd/mm/yyyy", Toast.LENGTH_LONG);
            dateWarning.show();
            return false;
        }
    }

    // Verifies is passwords match
    private boolean verifyPasswordsIdentical() {
        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText confirmPasswordText = getView().findViewById(R.id.signUpConfirmPassword);
        TextView passwordComment = getView().findViewById(R.id.signUpPasswordComment);
        System.out.println(passwordText.getText());
        System.out.println(confirmPasswordText.getText());
        System.out.println(passwordText.getText().equals(confirmPasswordText.getText()));
        if (passwordText.getText().toString().isEmpty() && confirmPasswordText.getText().toString().isEmpty()) {
            passwordComment.setVisibility(View.INVISIBLE);
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
        if (emailText.toString().contains("@")) {
            return true;
        } else {
            Toast dateWarning = Toast.makeText(getContext(), "Please make sure you provide a valid email address ", Toast.LENGTH_LONG);
            dateWarning.show();
            return false;
        }
    }

//    private void disableSingUp() {
//        Button signUpButton = getView().findViewById(R.id.signUpButton);
//        signUpButton.setBackgroundColor(NOT_CLICKABLE_COLOUR);
//        signUpButton.setEnabled(false);
//    }
//
//    private void enableSignUp() {
//        Button signUpButton = getView().findViewById(R.id.signUpButton);
//        signUpButton.setBackgroundColor(CLICKABLE_COLOUR);
//        signUpButton.setEnabled(true);
//    }

    // Verifies all user inputs
    private boolean verifyAllInputs() {
        boolean passed = true;
        passed = verifyPasswordsIdentical();
        passed = verifyDob();
        passed = verifyEmail();
        return passed;
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
            verifyAllInputs();
        }
    }
}