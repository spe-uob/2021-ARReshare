package com.example.ar_reshare;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginOptionFragment extends Fragment {

    public LoginOptionFragment() {
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
        View fragment = inflater.inflate(R.layout.fragment_login_option, container, false);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button loginButton = view.findViewById(R.id.loginOptionButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSignupActivity parent = (LoginSignupActivity) getActivity();
                parent.setCurrentFragment(2);
            }
        });

        Button signUpButton = view.findViewById(R.id.signUpOptionButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSignupActivity parent = (LoginSignupActivity) getActivity();
                parent.setCurrentFragment(0);
            }
        });
    }
}