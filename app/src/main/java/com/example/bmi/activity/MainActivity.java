package com.example.bmi.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.bmi.R;
import com.example.bmi.fragment.BmiFragment;
import com.example.bmi.fragment.ProfileFragment;
import com.example.bmi.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {
    ImageView iv_home,iv_profile,iv_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_home = findViewById(R.id.IV_Home);
        iv_profile = findViewById(R.id.IV_Logout);
        iv_post = findViewById(R.id.IV_Post);

        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(BmiFragment.class.getSimpleName());

        if (!(fragment instanceof HomeFragment)){
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container, homeFragment)
                    .commit();
        }
        iv_home.setOnClickListener(v -> {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container,homeFragment)
                    .addToBackStack(null)
                    .commit();
        });
        iv_post.setOnClickListener(v -> {
            BmiFragment bmiFragment = new BmiFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, bmiFragment)
                    .addToBackStack(null)
                    .commit();
        });
        iv_profile.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });


    }

}