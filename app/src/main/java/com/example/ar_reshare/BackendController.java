package com.example.ar_reshare;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.internal.GsonBuildConfig;

import java.util.List;
import java.util.Optional;

import de.javagl.obj.Obj;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendController {
    // Possible Status Codes
    private static final int SUCCESS = 200;
    private static final int SUCCESSFUL_CREATION = 201;
    private static final int INCORRECT_FORMAT = 400;
    private static final int INCORRECT_CREDENTIALS = 401;
    private static final int EMAIL_ADDRESS_ALREADY_REGISTERED = 409;
    private static final int PASSWORD_NOT_STRONG = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int TYPE_NOT_SUPPORTED = 422;


    private static final String URL = "https://ar-reshare.herokuapp.com/";
    private static String JWT; // Used for authentication

    private static boolean initialised = false;
    private static Context context;

    private BackendController() {}

    // Interface for callback handlers to receive response from the request
    public interface BackendCallback {
        void onBackendResult(boolean success, String message);
    }

    // Interface for callback handlers to receive response from the request
    public interface BackendSearchResultCallback {
        void onBackendSearchResult(boolean success, List<Product> searchResults);
    }

    private static void initialise() {
        Optional<Account> account = AuthenticationService.isLoggedIn(context);
        if (account.isPresent()) {
            // Send login request to get JWT
            AuthenticationService.loginUser(context, account.get(), new BackendCallback() {
                @Override
                public void onBackendResult(boolean success, String message) {
                    if (!success) {
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
        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.loginAccount(body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("Response back");
                    if (response.code() == SUCCESS) {
                        JWT = response.headers().get("Authorization");
                        System.out.println(JWT);
                        initialised = true;
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "Your email or password are incorrect");
                    } else {
                        callback.onBackendResult(false, "Failed to login");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to login");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            callback.onBackendResult(false, "Failed to login");
        }
    }

    public static boolean registerAccount(String name, String email, String password, String dob, String postcode, BackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        String bodyString =
                String.format("{\n  \"name\": \"%s\",\n  \"email\": \"%s\",\n  \"password\": \"%s\",\n  \"dob\": \"%s\"\n}",
                        name, email, password, dob);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.createAccount(body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESSFUL_CREATION) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == EMAIL_ADDRESS_ALREADY_REGISTERED) {
                        callback.onBackendResult(false, "This email address is already registered to another account");
                    } else {
                        callback.onBackendResult(false, "Failed to register new user");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to register new user");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    // TODO: Add a timer after which this method is called automatically
    public static boolean requestRegeneratedToken(BackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.requestRegeneratedToken(JWT);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESSFUL_CREATION) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == EMAIL_ADDRESS_ALREADY_REGISTERED) {
                        callback.onBackendResult(false, "The authentication token is missing or invalid");
                    } else {
                        callback.onBackendResult(false, "Failed to regenerate a new token");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to regenerate a new token");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }


    public static boolean addProduct(String title, String description, String country, String region, String postcode, Integer categoryID, String condition, List<String> media, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);

        JSONObject location = new JSONObject();
        location.put("country", country);
        location.put("region", region);
        location.put("postcode", postcode);
        json.put("location", location);

        json.put("categoryID", categoryID);
        json.put("condition", condition);
        JSONArray pics = new JSONArray();
        for (String pic : media) {
            pics.put(pic);
        }
        json.put("media",pics);
        String bodyString = json.toString();
        System.out.println(bodyString);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);
        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.addProduct(JWT, body);
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESSFUL_CREATION) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token is missing or invalid");
                    } else if(response.code() == RESOURCE_NOT_FOUND){
                        callback.onBackendResult(false, "A requested auxiliary resource (category, address) does not exist or is unavailable to you");
                    } else if (response.code() == TYPE_NOT_SUPPORTED){
                        callback.onBackendResult(false, "The media provided is not a supported file type");
                    } else {
                        callback.onBackendResult(false, "Failed to regenerate a new token????");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to regenerate a new token");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean searchListings(int startResults, int maxResults, BackendSearchResultCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService service = retrofit.create(BackendService.class);
        Call<Product.SearchResults> call = service.searchListings(maxResults, startResults);

        try {
            call.enqueue(new Callback<Product.SearchResults>() {
                @Override
                public void onResponse(Call<Product.SearchResults> call, Response<Product.SearchResults> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESS) {
                        callback.onBackendSearchResult(true, response.body().getSearchedProducts());
                    } else {
                        callback.onBackendSearchResult(false, null);
                    }
                }

                @Override
                public void onFailure(Call<Product.SearchResults> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendSearchResult(false, null);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }
}
