package com.example.bmi.models;

import com.google.gson.annotations.SerializedName;

public class BmiModel {
    private  int id;
    @SerializedName("bmi")
    private float bmi;
    private String judul;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    @SerializedName("weight")
    private String weight;

    @SerializedName("height")
    private String height;

    public BmiModel(int id, float bmi, String judul, String height, String weight, String createdAt, String updatedAt, String weightCategory) {
        this.id = id;
        this.bmi = bmi;
        this.judul = judul;
        this.height = height;
        this.weight = weight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.weightCategory = weightCategory;
    }

    @SerializedName("weightCategory")
    private String weightCategory;
    private String createdAt;
    private String updatedAt;
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }



    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeightCategory() {
        return weightCategory;
    }

    public void setWeightCategory(String weightCategory) {
        this.weightCategory = weightCategory;
    }
}
