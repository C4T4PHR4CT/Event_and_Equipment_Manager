package com.SovietHouseholdAppliances.EventManager.model;

import retrofit2.Call;
import retrofit2.http.*;

public interface EventManagerApi {
    @GET("token")
    Call<String> getToken(@Header("Authorization") String authorization);
    @GET("user/{user}")
    Call<User> getUser(@Header("Authorization") String authorization, @Path("user") String username);
}