package com.example.bmi.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bmi.DBHelper;
import com.example.bmi.HomeAdapter;
import com.example.bmi.R;
import com.example.bmi.models.BmiModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvSearch;
    private TextView tvNoData, tvConnectionLost;
    private SearchView searchView;
    private Button btnRetry;
    private DBHelper dbHelper;
    private List<BmiModel> notes;
    private HomeAdapter homeAdapter;
    private LottieAnimationView lottieProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvSearch = view.findViewById(R.id.recyclerView);
        tvNoData = view.findViewById(R.id.tv_dataKosong);
        tvConnectionLost = view.findViewById(R.id.connectionlost);
        searchView = view.findViewById(R.id.search);
        btnRetry = view.findViewById(R.id.btn_retry);
        lottieProgressBar = view.findViewById(R.id.lottieProgressBar);

        dbHelper = new DBHelper(getContext());

        notes = new ArrayList<>();
        homeAdapter = new HomeAdapter(getContext(), notes);
        rvSearch.setAdapter(homeAdapter);
        rvSearch.setLayoutManager(new GridLayoutManager(getContext(), 1));

        loadData("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(newText);
                return true;
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData("");
            }
        });

        return view;
    }

    private void loadData(String query) {
        // Tampilkan progress bar terlebih dahulu
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();
        rvSearch.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        tvConnectionLost.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);

        // Gunakan Handler untuk memberikan delay sebelum menampilkan hasil
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInternetAvailable()) {
                    lottieProgressBar.setVisibility(View.GONE);
                    tvConnectionLost.setVisibility(View.VISIBLE);
                    btnRetry.setVisibility(View.VISIBLE);
                    return;
                }

                notes.clear();
                Cursor cursor;
                if (query.isEmpty()) {
                    cursor = dbHelper.getAllRecords();
                } else {
                    cursor = dbHelper.searchByTitle(query);
                }

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.getColumnId()));
                        float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow(dbHelper.getColumnBmi()));
                        String judul = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnJudul()));
                        float tinggi = cursor.getFloat(cursor.getColumnIndexOrThrow(dbHelper.getColumnHeight()));
                        float berat = cursor.getFloat(cursor.getColumnIndexOrThrow(dbHelper.getColumnWeight()));
                        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnCreatedAt()));
                        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnUpdatedAt()));
                        String weightCategory = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnWeightCategory()));

                        BmiModel bmiModel = new BmiModel(id, bmi, judul, Float.toString(tinggi), Float.toString(berat), createdAt, updatedAt, weightCategory);
                        notes.add(bmiModel);
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                lottieProgressBar.setVisibility(View.GONE);

                if (notes.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvSearch.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    rvSearch.setVisibility(View.VISIBLE);
                }

                homeAdapter.notifyDataSetChanged();
            }
        }, 2000); // Delay 2 detik untuk menampilkan progress bar
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData("");
    }
}
