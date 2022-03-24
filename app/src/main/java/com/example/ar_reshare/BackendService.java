package com.example.ar_reshare;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import java.util.Optional;

public class BackendService {
    private static String JWT;
    private static boolean initialised = false;
    private static Context context;

    private BackendService() {}

    private static void initialise() {
        Optional<Account> account = AuthenticationService.isLoggedIn(context);
        if (account.isPresent()) {
            // Send login request to get JWT
            initialised = true;
        } else {
            // Open the login/signup screen
            Intent intent = new Intent(context, ARActivity.class);
            context.startActivity(intent);
        }
    }

    private static void loginUser() {

    }
}
