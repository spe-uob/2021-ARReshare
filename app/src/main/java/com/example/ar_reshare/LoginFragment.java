package com.example.ar_reshare;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Optional;

public class LoginFragment extends Fragment {

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
    private boolean loginUser() {
        if (false) {
            //AuthenticationService.addAccount(getContext(), )

            AlertDialog.Builder successful = new AlertDialog.Builder(getContext());
            successful.setTitle("Login Successful!");
            successful.setMessage("You have successfully logged in.");
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
            return true;
        } else {
        }
        return false;
    }
}