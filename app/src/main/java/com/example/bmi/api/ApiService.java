package com.example.bmi.api;

import com.example.bmi.models.BmiCategory;
import com.example.bmi.models.BmiModel;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {

    String RAPID_API_KEY = "f7882c082dmshdc8b5554aca7e00p109998jsn1bcb2e8fe37f";
    String RAPID_API_HOST = "body-mass-index-bmi-calculator.p.rapidapi.com";

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("metric")
    Call<BmiModel> getBmiNumber(@Query("weight") float weight,@Query("height") float height);
    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("weight-category")
    Call<BmiCategory> getBmiCategory(@Query("bmi") float bmi);
}

