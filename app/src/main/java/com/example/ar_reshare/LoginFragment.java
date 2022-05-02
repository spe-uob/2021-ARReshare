package com.example.ar_reshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment implements BackendController.BackendCallback {

    private final int DIALOG_TIME = 3000; // milliseconds

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button loginButton = getView().findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    // TODO: Send user login request to backend
    private void loginUser() {
        // Signal to the user that login is happening
        Button loginButton = getView().findViewById(R.id.loginButton);
        loginButton.setText("Logging in...");

        System.out.println("Login pressed");
        EditText emailText = getView().findViewById(R.id.loginEmail);
        EditText passwordText = getView().findViewById(R.id.loginPassword);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Send a login request to backend
        BackendController.loginAccount(email, password, this);
    }

    private void displaySuccess() {
        // Show successful dialog
        System.out.println("Display success");
        AlertDialog.Builder successful = new AlertDialog.Builder(getContext());
        successful.setTitle("Login Successful!");
        successful.setMessage("You have successfully logged in.");
        successful.setCancelable(false);
        AlertDialog dialog = successful.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
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
            }
        });
        dialog.show();
        Intent intent = new Intent(getContext(), SwipeActivity.class);
        startActivity(intent);
    }

    private void displayFailure() {
        Toast unsuccessful = Toast.makeText(getContext(), "Login failed! Incorrect email or password!", Toast.LENGTH_LONG);
        unsuccessful.show();

        // Change button text to default
        Button loginButton = getView().findViewById(R.id.loginButton);
        loginButton.setText("Login");
    }

    @Override
    public void onBackendResult(boolean success, String message) {
        if (success) {
            System.out.println("Successful login");

            EditText emailText = getView().findViewById(R.id.loginEmail);
            EditText passwordText = getView().findViewById(R.id.loginPassword);
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            // Add this AR-Reshare account to account manager
            Pair<byte[], byte[]> encryptedPair = Crypto.encrypt(password);
            AuthenticationService.addAccount(getContext(), email, encryptedPair.first, encryptedPair.second);

            displaySuccess();
        } else {
            System.out.println("Failure");
            displayFailure();
        }
    }
}