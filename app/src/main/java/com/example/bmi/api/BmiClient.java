package com.example.bmi.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class BmiClient {
    private static final String BASE_URL = "https://body-mass-index-bmi-calculator.p.rapidapi.com/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}