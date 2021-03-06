package com.example.ar_reshare;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Base64;
import java.util.Optional;

public class AuthenticationService extends Service {

    private static final Object lock = new Object();
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (lock) {
            if (mAuthenticator == null) {
                mAuthenticator = new Authenticator(this);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    // Checks if there exists an AR-Reshare account on the device, and returns it if so
    // (Checks if the user is logged in)
    public static Optional<Account> isLoggedIn(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        android.accounts.Account[] accounts = accountManager.getAccountsByType("com.example.ar_reshare");
        System.out.println("Number of accounts = " + accounts.length);
        if (accounts.length < 1) return Optional.empty();
        else return Optional.of(accounts[0]);
    }

    // Given an encrypted account, decrypts, and sends a login request through the BackendClient to the API
    public static boolean loginUser(Context context, Account account, BackendController.BackendCallback callbackHandler) {
        String password = getPassword(context);
        BackendController.loginAccount(account.name, password, callbackHandler);
        return false;
    }

    // Returns the plaintext password of the logged in account
    public static String getPassword(Context context) {
        Optional<Account> account = isLoggedIn(context);
        if (account.isPresent()) {
            AccountManager accountManager = AccountManager.get(context);

            String password = accountManager.getPassword(account.get());
            String iv = accountManager.getUserData(account.get(), "iv");

            byte[] passwordBytes = Base64.getDecoder().decode(password);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            String plaintextPassword = Crypto.decrypt(passwordBytes, ivBytes);
            return plaintextPassword;
        }
        return "";
    }

    // Adds a new AR-Reshare account, given encrypted password, to the Account Manager
    public static boolean addAccount(Context context, String email, byte[] encryptedPassword, byte[] iv) {
        // Remove any accounts before adding a new one
        AuthenticationService.removeAccounts(context);

        AccountManager accountManager = AccountManager.get(context);
        android.accounts.Account account = new Account(email, "com.example.ar_reshare");
        accountManager.addAccountExplicitly(account, Base64.getEncoder().encodeToString(encryptedPassword), null);
        accountManager.setUserData(account, "iv", Base64.getEncoder().encodeToString(iv));
        return true;
    }

    // Deletes (logs out from) all AR-Reshare accounts on the device
    public static boolean removeAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        android.accounts.Account[] accounts = accountManager.getAccounts();
        for (android.accounts.Account account : accounts) {
            System.out.println("Deleting existing account");
            accountManager.removeAccountExplicitly(account);
        }
        return true;
    }
}
