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

    public boolean isLoggedIn() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.example.ar_reshare");
        if (accounts.length < 1) return false;
        else {
            // TODO: Send login request to backend and update key
            System.out.println(accounts[0].name);
            String password = accountManager.getPassword(accounts[0]);
            String iv = accountManager.getUserData(accounts[0], "iv");
            System.out.println("RAW");
            System.out.println(password);
            System.out.println("DECRYPTED");
            byte[] passwordBytes = Base64.getDecoder().decode(password);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            System.out.println(ivBytes);
            System.out.println(Crypto.decrypt(passwordBytes, ivBytes));
            return true;
        }
    }

    public boolean addAccount(String email, byte[] encryptedPassword, byte[] iv) {
        AccountManager accountManager = AccountManager.get(this);
        // temporarily delete all existing accounts
        Account[] accounts = accountManager.getAccounts();
        for (Account account : accounts) {
            System.out.println("Deleting existing account");
            accountManager.removeAccountExplicitly(account);
        }

        Account account = new Account(email, "com.example.ar_reshare");
        accountManager.addAccountExplicitly(account, Base64.getEncoder().encodeToString(encryptedPassword), null);
        accountManager.setUserData(account, "iv", Base64.getEncoder().encodeToString(iv));
        return true;
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new SignUpFragment();
                case 1:
                    return new LoginOptionFragment();
                default:
                    return new LoginFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}