package com.example.bmi.models;

import com.google.gson.annotations.SerializedName;

public class BmiCategory {
    @SerializedName("bmi")
    private float bmi;

    @SerializedName("weightCategory")
    private String weightCategory;

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getWeightCategory() {
        return weightCategory;
    }

    public void setWeightCategory(String weightCategory) {
        this.weightCategory = weightCategory;
    }
}
