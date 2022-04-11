package com.example.ar_reshare;

import java.util.List;

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
    @GET("listings/search")
    Call<Product.SearchResults> searchListings(@Query("maxResults") int maxResults,
                                               @Query("startResults") int startResults);

    @Headers("Content-Type: application/json")
    @PUT("token/regeneration")
    Call<ResponseBody> requestRegeneratedToken(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("listing/create")
    Call<ResponseBody> addProduct(@Header("Authorization") String token, @Body RequestBody data);

    @Headers("Content-Type: application/json")
    @GET("listing/view")
    Call<Product> getListingByID(@Query("listingID") int listingID);

    @Headers("Content-Type: application/json")
    @PATCH("listing/close")
    Call<ResponseBody> closeListing(@Header("Authorization") String token, @Body RequestBody data);
}
