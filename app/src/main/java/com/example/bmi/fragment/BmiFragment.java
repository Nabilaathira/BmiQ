package com.example.bmi.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bmi.DBHelper;
import com.example.bmi.R;
import com.example.bmi.api.ApiService;
import com.example.bmi.api.BmiClient;
import com.example.bmi.models.BmiCategory;
import com.example.bmi.models.BmiModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BmiFragment extends Fragment {

    private EditText weightInput, heightInput, judulInput;
    private Button calculateButton, retryButton;
    private TextView resultText, connectionLostText;
    private ApiService apiService;
    private DBHelper dbHelper;
    private LinearLayout allbmi;
    private LottieAnimationView lottieProgressBar, animationView;

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bmi, container, false);

        judulInput = view.findViewById(R.id.title_input);
        weightInput = view.findViewById(R.id.weight_input);
        heightInput = view.findViewById(R.id.height_input);
        calculateButton = view.findViewById(R.id.calculate_button);
        lottieProgressBar = view.findViewById(R.id.lottieProgressBar);
        animationView = view.findViewById(R.id.animationView);
        resultText = view.findViewById(R.id.result_text);
        connectionLostText = view.findViewById(R.id.connectionlost);
        retryButton = view.findViewById(R.id.btn_retry);
        allbmi = view.findViewById(R.id.allbmi);

        apiService = BmiClient.getClient().create(ApiService.class);
        dbHelper = new DBHelper(getContext());
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInternetAvailable()) {
                    lottieProgressBar.setVisibility(View.GONE);
                    connectionLostText.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.VISIBLE);
                    allbmi.setVisibility(View.GONE);

                }
                lottieProgressBar.setVisibility(View.GONE);
                allbmi.setVisibility(View.VISIBLE);
            }
            },2000);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBMI();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBMI();
            }
        });

        return view;
    }

    private void performBMI() {
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();
        connectionLostText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        allbmi.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInternetAvailable()) {
                    lottieProgressBar.setVisibility(View.GONE);
                    connectionLostText.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.VISIBLE);
                    allbmi.setVisibility(View.GONE);
                    return;
                }
                lottieProgressBar.setVisibility(View.GONE);

                String judulStr = judulInput.getText().toString();
                String weightStr = weightInput.getText().toString();
                String heightStr = heightInput.getText().toString();

                if (judulStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your weight, height, and title", Toast.LENGTH_SHORT).show();
                } else {
                    float weight = Float.parseFloat(weightStr);
                    float height = Float.parseFloat(heightStr) / 100; // convert height to meters

                    Call<BmiModel> call = apiService.getBmiNumber(weight, height);
                    call.enqueue(new Callback<BmiModel>() {
                        @Override
                        public void onResponse(Call<BmiModel> call, Response<BmiModel> response) {
                            if (response.isSuccessful()) {
                                BmiModel bmiResponse = response.body();
                                if (bmiResponse != null) {
                                    float bmi = bmiResponse.getBmi();
                                    getCategory(bmi, judulStr, weightStr, heightStr);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Failed to get BMI response", Toast.LENGTH_SHORT).show();
                                lottieProgressBar.setVisibility(View.GONE);
                                lottieProgressBar.cancelAnimation();
                            }
                        }

                        @Override
                        public void onFailure(Call<BmiModel> call, Throwable t) {
                            Toast.makeText(getActivity(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            lottieProgressBar.setVisibility(View.GONE);
                            lottieProgressBar.cancelAnimation();
                        }
                    });
                }
            }

            private void getCategory(float bmi, String judul, String weight, String height) {
                Call<BmiCategory> call = apiService.getBmiCategory(bmi);
                call.enqueue(new Callback<BmiCategory>() {
                    @Override
                    public void onResponse(Call<BmiCategory> call, Response<BmiCategory> response) {
                        if (response.isSuccessful()) {
                            BmiCategory bmiCategoryResponse = response.body();
                            if (bmiCategoryResponse != null) {
                                String weightCategory = bmiCategoryResponse.getWeightCategory();
                                saveDataToDatabase(bmi, weightCategory, judul, weight, height);
                                showResult(bmi, weightCategory);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to get category response", Toast.LENGTH_SHORT).show();
                        }
                        lottieProgressBar.setVisibility(View.GONE);
                        lottieProgressBar.cancelAnimation();
                    }

                    @Override
                    public void onFailure(Call<BmiCategory> call, Throwable t) {
                        Toast.makeText(getActivity(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        lottieProgressBar.setVisibility(View.GONE);
                        lottieProgressBar.cancelAnimation();
                    }
                });
            }
        }, 2000);
    }

    private void saveDataToDatabase(float bmi, String weightCategory, String judul, String weight, String height) {
        dbHelper.insertData(judul, Float.parseFloat(weight), Float.parseFloat(height), bmi, weightCategory);
    }

    private void showResult(float bmi, String weightCategory) {
        resultText.setText(String.format("Your BMI: %.2f\nWeight Category: %s", bmi, weightCategory));
        resultText.setVisibility(View.VISIBLE);
        allbmi.setVisibility(View.VISIBLE);
    }

    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
