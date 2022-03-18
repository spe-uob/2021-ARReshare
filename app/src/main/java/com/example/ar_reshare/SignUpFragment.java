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

public class SignUpFragment extends Fragment {

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

    private void changeDateField(String text) {
        EditText dobText = getView().findViewById(R.id.signUpDateOfBirth);
        dobText.setText(text);
    }

    private void checkPasswordsIdentical() {
        EditText passwordText = getView().findViewById(R.id.signUpPassword);
        EditText confirmPasswordText = getView().findViewById(R.id.signUpConfirmPassword);
        System.out.println(passwordText.getText());
        System.out.println(confirmPasswordText.getText());
        System.out.println(passwordText.getText().equals(confirmPasswordText.getText()));
        if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
            System.out.println("PASSWORDS DO NOT MATCH");
            passwordText.setBackgroundColor(Color.RED);
            confirmPasswordText.setBackgroundColor(Color.RED);
        } else {
            System.out.println("PASSWORDS MATCH");
            passwordText.setBackgroundColor(Color.GREEN);
            confirmPasswordText.setBackgroundColor(Color.GREEN);
        }
    }

    private class dateChangedListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            changeDateField(dayOfMonth + "/" + month + "/" + year);

        }
    }

    private class passwordChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkPasswordsIdentical();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}