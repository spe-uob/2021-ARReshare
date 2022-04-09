package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.accounts.AccountManager;
import android.accounts.Account;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Base64;

public class LoginSignupActivity extends AppCompatActivity {

    private final int OPTION_FRAGMENT = 1;
    private final int LOGIN = 2;
    private final int SIGN_UP = 0;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        this.viewPager = findViewById(R.id.loginOptionViewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        setCurrentFragment(OPTION_FRAGMENT);
    }

    public void setCurrentFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case SIGN_UP:
                    return new SignUpFragment();
                case LOGIN:
                    return new LoginFragment();
                default:
                    return new LoginOptionFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}