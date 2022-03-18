package com.example.ar_reshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, viewPager);
        viewPager.setAdapter(adapter);
        setCurrentFragment(OPTION_FRAGMENT);
    }

    public void setCurrentFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        private ViewPager2 viewPager;
        private Fragment currentFragment;

        ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ViewPager2 viewPager) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new SignUpFragment();
                case 1:
                    return new LoginOptionFragment(viewPager);
                default:
                    return new LoginFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public Fragment getCurrentFragment() {
            return currentFragment;
        }
    }
}