package com.example.ar_reshare;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class PostcodeHelper {
    // Possible Status Codes
    private static final int SUCCESS = 200;
    private static final int INCORRECT_FORMAT = 400;
    private static final int POSTCODE_NOT_FOUND = 404;

    // Default values if postcode not found
    private static final String DEFAULT_POSTCODE = "BS81UB";
    private static final String DEFAULT_CITY = "Bristol, City of";
    private static final String DEFAULT_COUNTRY = "England";
    private static final float DEFAULT_LATITUDE = 51.4559f;
    private static final float DEFAULT_LONGTIUDE = -2.603f;

    private static final String POSTCODE_API_URL = "https://postcodes.io/";

    private PostcodeHelper() {}

    // Service to extract information from a postcode such as coordinates
    // Uses the open-source Postcodes.io API
    private interface PostcodeService {
        @GET("postcodes/{postcode}")
        Call<PostcodeResponse> lookupPostcode(@Path("postcode") String postcode);

        @GET("postcodes")
        Call<CoordinateResponse> lookupCoordinates(@Query("lat") String latitude, @Query("lon") String longitude);
    }

    // Callback Interface
    public interface PostcodeCallback {
        void onPostcodeResult(boolean success, PostcodeDetails response);
    }

    // Given a postcode, find more information about the location
    public static void lookupPostcode(String postcode, PostcodeCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(POSTCODE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostcodeService service = retrofit.create(PostcodeService.class);
        Call<PostcodeResponse> call = service.lookupPostcode(postcode);
        call.enqueue(new Callback<PostcodeResponse>() {
            @Override
            public void onResponse(Call<PostcodeResponse> call, Response<PostcodeResponse> response) {
                if (response.code() == SUCCESS) {
                    PostcodeDetails postcodeDetails = response.body().getResult();
                    callback.onPostcodeResult(true, postcodeDetails);
                } else if (response.code() == POSTCODE_NOT_FOUND) {
                    callback.onPostcodeResult(true, defaultPostcode());
                } else {
                    callback.onPostcodeResult(false, null);
                }
            }

            @Override
            public void onFailure(Call<PostcodeResponse> call, Throwable t) {
                callback.onPostcodeResult(false, null);
            }
        });
    }

    // Given a pair of coordinates, find more information about the location
    public static void lookupCoordinates(String latitude, String longitude, PostcodeCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(POSTCODE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostcodeService service = retrofit.create(PostcodeService.class);
        Call<CoordinateResponse> call = service.lookupCoordinates(latitude, longitude);
        call.enqueue(new Callback<CoordinateResponse>() {
            @Override
            public void onResponse(Call<CoordinateResponse> call, Response<CoordinateResponse> response) {
                if (response.code() == SUCCESS) {
                    if (!response.body().getResult().isEmpty()) {
                        callback.onPostcodeResult(true, response.body().getResult().get(0));
                    } else {
                        callback.onPostcodeResult(false, null);
                    }
                } else {
                    callback.onPostcodeResult(false, null);
                }
            }

            @Override
            public void onFailure(Call<CoordinateResponse> call, Throwable t) {
                callback.onPostcodeResult(false, null);
            }
        });
    }

    private static PostcodeDetails defaultPostcode() {
        PostcodeDetails defaultPostcode = new PostcodeDetails();
        defaultPostcode.setPostcode(DEFAULT_POSTCODE);
        defaultPostcode.setCity(DEFAULT_CITY);
        defaultPostcode.setCountry(DEFAULT_COUNTRY);
        defaultPostcode.setLatitude(DEFAULT_LATITUDE);
        defaultPostcode.setLongitude(DEFAULT_LONGTIUDE);
        return defaultPostcode;
    }

}
