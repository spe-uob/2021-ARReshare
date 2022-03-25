package com.example.ar_reshare;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;


import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BackendClient {
    private static final int SUCCESS = 200;
    private static final int SUCCESSFUL_CREATION = 201;
    private static final int INCORRECT_FORMAT = 400;
    private static final int INCORRECT_CREDENTIALS = 401;
    private static final int EMAIL_ADDRESS_ALREADY_REGISTERED = 409;
    private static final int PASSWORD_NOT_STRONG = 422;

    private static final String URL = "https://ar-reshare.herokuapp.com/";
    private static String JWT;
    private static boolean initialised = false;
    private static Context context;

    private BackendClient() {}

    public interface BackendCallback {
        void onBackendResult(boolean result);
    }

    private static void initialise() {
        Optional<Account> account = AuthenticationService.isLoggedIn(context);
        if (account.isPresent()) {
            // Send login request to get JWT
            AuthenticationService.loginUser(context, account.get(), new BackendCallback() {
                @Override
                public void onBackendResult(boolean result) {
                    if (!result) {
                        // Open the login/signup screen
                        Intent intent = new Intent(context, LoginSignupActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            // Open the login/signup screen
            Intent intent = new Intent(context, LoginSignupActivity.class);
            context.startActivity(intent);
        }
    }

    public static void loginAccount(String email, String password, BackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        String bodyString = String.format("{\n  \"email\": \"%s\",\n  \"password\": \"%s\"\n}", email, password);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);
        BackendAPI service = retrofit.create(BackendAPI.class);
        Call<ResponseBody> call = service.loginAccount(body);
        boolean result = false;

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("Response back");
                    if (response.code() == SUCCESS) {
                        JWT = response.headers().get("Authorization");
                        initialised = true;
                        callback.onBackendResult(true);
                    } else if (response.code() == EMAIL_ADDRESS_ALREADY_REGISTERED) {
                        callback.onBackendResult(false);
                    } else {
                        callback.onBackendResult(false);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            callback.onBackendResult(false);
        }
    }

    public static boolean registerAccount(String name, String email, String password, String dob) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        String bodyString =
                String.format("{\\n  \\\"name\\\": \\\"%s\\\",\\n  \\\"email\\\": \\\"%s\\\"," +
                        "\\n  \\\"password\\\": \\\"%s\\\",\\n  \\\"dob\\\": \\\"%s\\\"\\n}",
                        name, email, password, dob);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendAPI service = retrofit.create(BackendAPI.class);

        Call<ResponseBody> call = service.registerAccount(body);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == SUCCESSFUL_CREATION) {
                return true;
            } else if (response.code() == EMAIL_ADDRESS_ALREADY_REGISTERED) {
                return false;
            } else if (response.code() == PASSWORD_NOT_STRONG){
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean requestRegeneratedToken() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        BackendAPI service = retrofit.create(BackendAPI.class);

        Call<ResponseBody> call = service.requestRegeneratedToken("Bearer " + JWT);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == SUCCESS) {
                JWT = response.headers().get("Authorization");
                initialised = true;
                return true;
            } else if (response.code() == INCORRECT_CREDENTIALS) {
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
