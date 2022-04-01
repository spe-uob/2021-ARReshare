package com.example.ar_reshare;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;


import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
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
    private static final int REQUEST_NOT_FOUND = 404;
    private static final int EMAIL_ADDRESS_ALREADY_REGISTERED = 409;
    private static final int PASSWORD_NOT_STRONG = 422;
    private static final int MEDIA_NOT_SUPPORT = 422;


    private static final String URL = "https://ar-reshare.herokuapp.com/";
    private static String JWT; // Used for authentication

    private static boolean initialised = false;
    private static Context context;

    private BackendController() {}

    // Interface for callback handlers to receive response from the request
    public interface BackendCallback {
        void onBackendResult(boolean success, String message);
    }

    public interface ChatBackendCallback {
        void onBackendResult(boolean success, String message, Chat.ConversationsResult conversations);
    }

    public interface MessageBackendCallback {
        void onBackendResult(boolean success, String message, Message.MessageResult messageResult);
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

    public static boolean createConversation(Integer listingID, BackendCallback callback) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String bodyString =
                String.format("{\n  \"listingID\": \"%d\"\n}",
                        listingID);

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.createConversation(JWT,body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
//                    try {
//                        String string = response.body().string();
//                        String regex = "[^0-9]";
//                        Pattern p = Pattern.compile(regex);
//                        Matcher m = p.matcher(string);
//                        //String[] s = string.split(regex);
//                        int conversationId = Integer.valueOf(m.replaceAll("").trim());
//                        System.out.println("conversation id :"+conversationId);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    if (response.code() == SUCCESSFUL_CREATION) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == REQUEST_NOT_FOUND){
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

//        String bodyString =
//                String.format("{\n  \"name\": \"%d\"\n}",
//                        listingID);
//        RequestBody body =
//                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<Chat.ConversationsResult> call = service.getConversationDescriptors(JWT,10,0);

        try {
            call.enqueue(new Callback<Chat.ConversationsResult>() {
                @Override
                public void onResponse(Call<Chat.ConversationsResult> call, Response<Chat.ConversationsResult> response) {
                    System.out.println(response.code());
                    Chat.ConversationsResult conversations = response.body();
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success", conversations);
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The getConversationDescriptors was missing required parameters",conversations);
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid",conversations);
                    }
                }
                @Override
                public void onFailure(Call<Chat.ConversationsResult> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation",null);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean closeConversation(Integer conversationID, BackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        String bodyString =
                String.format("{\n  \"conversationID\": \"%d\"\n}",
                        conversationID);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.closeConversation(JWT, body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    if (response.code() == SUCCESSFUL_CREATION) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == REQUEST_NOT_FOUND){
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you");
                    } else {
                        callback.onBackendResult(false, "That resource is already closed");
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

    public static boolean getConversationByID(Integer conversationID, MessageBackendCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        String bodyString =
//                String.format("{\n  \"name\": \"%d\"\n}",
//                        listingID);
//        RequestBody body =
//                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<Message.MessageResult> call = service.getConversationByID(JWT,10,0, conversationID);

        try {
            call.enqueue(new Callback<Message.MessageResult>() {
                @Override
                public void onResponse(Call<Message.MessageResult> call, Response<Message.MessageResult> response) {
                    System.out.println(response.code());
                    Message.MessageResult messageResult = response.body();
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success", messageResult);
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The getConversationDescriptors was missing required parameters",null);
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid",null);
                    }else if (response.code() == MEDIA_NOT_SUPPORT) {
                        callback.onBackendResult(false, "The requested resource does not exist or is unavailable to you",null);
                    }
                }
                @Override
                public void onFailure(Call<Message.MessageResult> call, Throwable t) {
                    System.out.println("Failure");
                    callback.onBackendResult(false, "Failed to create new conversation",null);
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

        System.out.println(bodyString);
        System.out.println(JWT);

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), bodyString);

        BackendService service = retrofit.create(BackendService.class);
        Call<ResponseBody> call = service.sendConversationMessage(JWT,body);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());
                    ResponseBody responseBody = response.body();
                    if (response.code() == SUCCESS) {
                        callback.onBackendResult(true, "Success");
                    } else if (response.code() == INCORRECT_FORMAT) {
                        callback.onBackendResult(false, "The request was missing required parameters, or was formatted incorrectly");
                    } else if (response.code() == INCORRECT_CREDENTIALS) {
                        callback.onBackendResult(false, "The authentication token was missing or invalid");
                    } else if (response.code() == REQUEST_NOT_FOUND) {
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
}
