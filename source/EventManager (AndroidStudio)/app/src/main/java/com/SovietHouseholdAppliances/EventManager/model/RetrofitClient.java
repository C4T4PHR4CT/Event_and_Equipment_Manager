package com.SovietHouseholdAppliances.EventManager.model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;
    private static final String baseURL = "http://localhost:5000";

    public static Retrofit getClient() {
        if (instance ==null) {
            instance = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}