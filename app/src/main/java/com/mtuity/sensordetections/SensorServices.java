package com.mtuity.sensordetections;

import com.mtuity.sensordetections.reversegeocoding.SpaceAddressData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by kalyani on 23/8/17.
 */

public interface SensorServices {

    @GET("/{latAndLog}.json")
    void getAddressByCoordinates(@Path("latAndLog") String latandLogitude,
                                 @Query("access_token") String token,
                                 Callback<SpaceAddressData> callback);
}
