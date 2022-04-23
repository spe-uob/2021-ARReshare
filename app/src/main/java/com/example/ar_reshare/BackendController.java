package com.example.ar_reshare;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private static final int CONVERSATION_ALREADY_CLOSED = 409;
    private static final int MEDIA_NOT_SUPPORT = 422;
    private static final int REQUEST_NOT_FOUND = 404;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int TYPE_NOT_SUPPORTED = 422;


    private static final String URL = "https://ar-reshare.herokuapp.com/";
    private static String JWT; // Used for authentication
    private static int loggedInUserID;

    private static boolean initialised = false;
    private static Context context;

    private BackendController() {}

    // Interface for callback handlers to receive response from the request
    public interface BackendCallback {
        void onBackendResult(boolean success, String message);
    }

    public interface ChatBackendCallback {
        void onBackendResult(boolean success, String message, int loggedInUserID, Chat.ConversationsResult conversations);
    }

    public interface MessageBackendCallback {
        void onBackendResult(boolean success, String message, int loggedInUserID, Message.MessageResult messageResult);
    }

    // Interface for callback handlers to receive response from the request
    public interface BackendSearchResultCallback {
        void onBackendSearchResult(boolean success, List<Product> searchResults);

    }

    public interface BackendGetListingResultCallback {
        void onBackendGetListingResult(boolean success, Product ListingResult);}

    // Interface for callback handlers to receive response from the request
    public interface BackendProfileResultCallback {
        void onBackendProfileResult(boolean success, User userProfile);
    }

    public static int getLoggedInUserID() {
        return loggedInUserID;
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

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) { }

        String bodyString = json.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);
        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.loginAccount(body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("Response back");
                    if (response.code() == SUCCESS) {
                        JWT = response.headers().get("Authorization");
                        String[] parts = JWT.split("\\.");
                        // Decode the currently logged in user id from the JWT token
                        try {
                            JSONObject payload =
                                    new JSONObject(new String(Base64.getUrlDecoder().decode(parts[1])));
                            loggedInUserID = payload.getInt("userID");
                            System.out.println("Logged in account id = " + loggedInUserID);
                            System.out.println(JWT);
                            initialised = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public static void registerAccount(String name, String email, String password, String dob, String postcode, BackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("email", email);
            json.put("password", password);
            json.put("dob", dob);
        } catch (Exception e) { }

        String bodyString = json.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);
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
        }
    }

    public static void modifyAccount(Context context, Map<String, String> changes, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        String password;
        // If user attempts to modify password or email, old password must be provided
        if (changes.containsKey("newPassword") || changes.containsKey("email")) {
            password = changes.getOrDefault("password", "NO PASSWORD PROVIDED");
        } else {
            // If the change is less sensitive, the authentication service will provide the password
            password = AuthenticationService.getPassword(context);
        }

        JSONObject json = new JSONObject();
        json.put("password", password);

        for (Map.Entry<String, String> change : changes.entrySet()) {
            if (!change.getKey().equals("password")) {
                json.put(change.getKey(), change.getValue());
            }
        }

        String bodyString = json.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);
        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.modifyAccount(JWT, body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "");
                    }
                    else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "Incorrect password provided");
                    }
                    else if (response.code() == PASSWORD_NOT_STRONG) {
                        callback.onBackendResult(false, "Password not strong or age below minimum");
                    }
                    else callback.onBackendResult(false, "Failed to modify account");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onBackendResult(false, "Failed to modify account");
                }
            });
        } catch (Exception e) {
            callback.onBackendResult(false, e.getMessage());
        }
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


    public static boolean createConversation(Integer listingID, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listingID", listingID);
        String bodyString = jsonObject.toString();

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.createConversation(JWT,body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());

                    if (response.code() == SUCCESSFUL_CREATION) {
                        try {
                            String string = response.body().string();
                            String regex = "[^0-9]";
                            Pattern p = Pattern.compile(regex);
                            Matcher m = p.matcher(string);
                            int conversationId = Integer.valueOf(m.replaceAll("").trim());
                            callback.onBackendResult(true, String.valueOf(conversationId));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == RESOURCE_NOT_FOUND){
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you");
                    } else {
                        System.out.println("response"+response.message());
                        callback.onBackendResult(false, "That resource already exists");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean getConversationDescriptors(ChatBackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        BackendService service = retrofit.create(BackendService.class);
        Call<Chat.ConversationsResult> call = service.getConversationDescriptors(JWT,10,0);

        try {
            call.enqueue(new Callback<Chat.ConversationsResult>() {
                @Override
                public void onResponse(Call<Chat.ConversationsResult> call, Response<Chat.ConversationsResult> response) {
                    System.out.println(response.code());
                    Chat.ConversationsResult conversations = response.body();
                    System.out.println("conversations : " + conversations.getChats().size());
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success", loggedInUserID, conversations);
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The getConversationDescriptors was missing required parameters", loggedInUserID, conversations);
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid", loggedInUserID, conversations);
                    }
                }
                @Override
                public void onFailure(Call<Chat.ConversationsResult> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation", loggedInUserID, null);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean getConversationByID(Integer conversationID, MessageBackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        

        BackendService service = retrofit.create(BackendService.class);
        Call<Message.MessageResult> call = service.getConversationByID(JWT,10,0, conversationID);

        try {
            call.enqueue(new Callback<Message.MessageResult>() {
                @Override
                public void onResponse(Call<Message.MessageResult> call, Response<Message.MessageResult> response) {
                    System.out.println(response.code());
                    Message.MessageResult messageResult = response.body();
                    System.out.println("url is " + messageResult.getUrl());
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success", loggedInUserID,messageResult);
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false,"The getConversationDescriptors was missing required parameters", loggedInUserID,null);
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid",loggedInUserID,null);
                    }else if (response.code() == MEDIA_NOT_SUPPORT) {
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you", loggedInUserID,null);
                    }
                }
                @Override
                public void onFailure(Call<Message.MessageResult> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation", loggedInUserID,null);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean sendConversationMessage(Integer conversationID, String textContent, String mediaContent, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("conversationID", conversationID);
        jsonObject.put("textContent", textContent);
        jsonObject.put("mediaContent", mediaContent);
        String bodyString = jsonObject.toString();

        System.out.println(JWT);

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.sendConversationMessage(JWT, body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == RESOURCE_NOT_FOUND) {
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you");
                    } else if (response.code() == MEDIA_NOT_SUPPORT) {
                        callback.onBackendResult(false, "The media provided is not a supported file type");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean closeConversation(Integer conversationID, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("conversationID", conversationID);
        String bodyString = jsonObject.toString();

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.closeConversation(JWT, body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success close");
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == RESOURCE_NOT_FOUND) {
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you");
                    } else if (response.code() == CONVERSATION_ALREADY_CLOSED) {
                        callback.onBackendResult(false, "That resource is already closed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to close conversation");
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
                    } else if(response.code() == TYPE_NOT_SUPPORTED){
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


    public static void searchListings(int startResults, int maxResults, BackendSearchResultCallback callback) {
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
                        initialiseProducts(response.body().getSearchedProducts(), callback);
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
            System.out.println("Encountered error. " + e);
            callback.onBackendSearchResult(false, null);
        }
    }

    public static void getProfileByID(int startResults, int maxResults, int userID,
                                      BackendProfileResultCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService service = retrofit.create(BackendService.class);
        Call<User> call = service.getProfileByID(maxResults, startResults, userID);

        try {
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESS) {
                        initialiseProfilePic(response.body(), callback);
                    } else {
                        callback.onBackendProfileResult(false, null);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendProfileResult(false, null);
                }
            });
        } catch (Exception e) {
            System.out.println("Encountered error. " + e);
            callback.onBackendProfileResult(false, null);
        }
    }

    public static void createSavedListing(int listingID, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listingID", listingID);
        String bodyString = jsonObject.toString();

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.createSavedListing(JWT, body);
        System.out.println("Printing JWT " + JWT + " and listingID " + listingID);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(
                                true, "Listing with id " + listingID + " was saved");
                    } else {
                        callback.onBackendResult(
                                false, "Unsuccessful response code");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to save listing");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Helper method of searchListings()
    // Waits until all products have had their main photo downloaded and postcode converted into coordinates
    private static void initialiseProducts(List<Product> products, BackendSearchResultCallback callback) {
        final int NUMBER_OF_REQUESTS_PER_PRODUCT = 2;

        // Initialise the latch to wait for callbacks
        CountDownLatch latch = new CountDownLatch(products.size() * NUMBER_OF_REQUESTS_PER_PRODUCT);
        // Find coordinates for each product
        products.forEach(product -> product.findCoordinates(latch));
        // Download main photo for each product
        products.forEach(product -> product.downloadMainPicture(latch));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                callback.onBackendSearchResult(true, products);
            }
        }).start();

    }

    // Helper method of getProfileById()
    // Waits until the user has had their profile photo downloaded
    private static void initialiseProfilePic(User user, BackendProfileResultCallback callback) {
        // Initialise the latch to wait for callbacks
        CountDownLatch latch = new CountDownLatch(1);
        user.downloadProfilePicture(latch);
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onBackendProfileResult(true, user);
        }).start();
    }

    public static void getListingByID(Integer listingID, BackendGetListingResultCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService service = retrofit.create(BackendService.class);
        Call<Product> call = service.getListingByID(listingID);

        try {
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.code() == SUCCESS) {
                        System.out.println("get listing success");
                        initialiseProduct(response.body(), callback);
                    } else {
                        callback.onBackendGetListingResult(false, null);
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendGetListingResult(false, null);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //helper method of getListingByID
    //wait until the product has all of its images downloaded
    private static void initialiseProduct(Product product, BackendGetListingResultCallback callback) {
        final int NUMBER_OF_IMAGES = product.getProductMedia().size();
        // Initialise the latch to wait for callbacks
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_IMAGES);
        product.downloadAllPictures(latch);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                callback.onBackendGetListingResult(true, product);
            }
        }).start();
    }
}

