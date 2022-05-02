package com.example.ar_reshare;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface BackendService {

    @Headers("Content-Type: application/json")
    @POST("account/login")
    Call<ResponseBody> loginAccount(@Body RequestBody data);

    @Headers("Content-Type: application/json")
    @PUT("account/create")
    Call<ResponseBody> createAccount(@Body RequestBody data);

    @Headers("Content-Type: application/json")
    @PATCH("account/modify")
    Call<ResponseBody> modifyAccount(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @GET("listings/search")
    Call<Product.SearchResults> searchListings(@Header("Authorization") String token,
                                               @Query("maxResults") int maxResults,
                                               @Query("startResults") int startResults);

    @Headers("Content-Type: application/json")
    @GET("account/listings/search")
    Call<Product.SearchResults> searchAccountListings(@Header("Authorization") String token,
                                                      @Query("maxResults") int maxResults,
                                                      @Query("startResults") int startResults);

    @Headers("Content-Type: application/json")
    @GET("account/saved-listings/search")
    Call<Product.SearchResults> searchSavedListings(  @Header("Authorization") String token,
                                                      @Query("maxResults") int maxResults,
                                                      @Query("startResults") int startResults);


    @Headers("Content-Type: application/json")
    @GET("profile/view")
    Call<User> getProfileByID(@Query("maxResults") int maxResults,
                                     @Query("startResults") int startResults,
                                     @Query("userID") int userID);

    @Headers("Content-Type: application/json")
    @PUT("token/regeneration")
    Call<ResponseBody> requestRegeneratedToken(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("conversation/create")
    Call<ResponseBody> createConversation(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @GET("conversations")
    Call<Chat.ConversationsResult> getConversationDescriptors(@Header("Authorization") String token, @Query("maxResults") int maxResults,
                                                              @Query("startResults") int startResults);

    @Headers("Content-Type: application/json")
    @PATCH("conversation/close")
    Call<ResponseBody> closeConversation(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @GET("conversation/view")
    Call<Message.MessageResult> getConversationByID(@Header("Authorization") String token, @Query("maxResults") int maxResults,
                                                    @Query("startResults") int startResults, @Query("conversationID") int conversationID);

    @Headers("Content-Type: application/json")
    @PUT("conversation/message")
    Call<ResponseBody> sendConversationMessage(@Header("Authorization") String token, @Body RequestBody data);

    @PUT("listing/create")
    Call<ResponseBody> addProduct(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @GET("listing/view")
    Call<Product> getListingByID(@Query("listingID") int listingID);

    @Headers("Content-Type: application/json")
    @POST("account/saved-listings/create")
    Call<ResponseBody> createSavedListing(@Header("Authorization") String token,
                                          @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @POST("account/saved-listings/delete")
    Call<ResponseBody> deleteSavedListing(@Header("Authorization") String token,
                                          @Body RequestBody data);

    @PATCH("listing/close")
    Call<ResponseBody> closeListing(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @PATCH("listing/modify")
    Call<ResponseBody> modifyListing(@Header("Authorization") String token, @Body RequestBody data);
}
