package com.example.ar_reshare;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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
    @PUT("token/regeneration")
    Call<ResponseBody> requestRegeneratedToken(@Header("Authorization") String token);
}
