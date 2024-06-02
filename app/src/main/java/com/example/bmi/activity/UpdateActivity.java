package com.example.bmi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class UpdateActivity extends AppCompatActivity {
    private EditText et_UpdateJudul;
    private EditText et_Updateweight, et_Updateheight;
    private Button btn_update,btnretry;
    private LottieAnimationView lottieProgressBar;
    private TextView connectionlost;
    private LinearLayout allupdate;
    private DBHelper dbHelper;
    private int recordId;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new DBHelper(this);

        et_UpdateJudul = findViewById(R.id.et_upjudul);
        et_Updateweight = findViewById(R.id.et_weight);
        et_Updateheight = findViewById(R.id.et_height);
        btn_update = findViewById(R.id.btn_update);
        btnretry = findViewById(R.id.btn_retry);
        lottieProgressBar = findViewById(R.id.lottieProgressBar);
        connectionlost = findViewById(R.id.connectionlost);
        allupdate = findViewById(R.id.allupdate);

        lottieProgressBar.setVisibility(View.GONE);
        connectionlost.setVisibility(View.GONE);
        btnretry.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("record_id")) {
            recordId = intent.getIntExtra("record_id", -1);
            loadRecordData(recordId);
        }

        findViewById(R.id.btn_backk).setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
        allupdate.setVisibility(View.GONE);
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isNetworkAvailable()) {
                    lottieProgressBar.setVisibility(View.GONE);
                    connectionlost.setVisibility(View.VISIBLE);
                    btnretry.setVisibility(View.VISIBLE);
                    allupdate.setVisibility(View.GONE);

                }
                lottieProgressBar.setVisibility(View.GONE);
                allupdate.setVisibility(View.GONE);
            }
        }, 2000);

        btnretry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieProgressBar.setVisibility(View.VISIBLE);
                lottieProgressBar.playAnimation();
                connectionlost.setVisibility(View.GONE);
                btnretry.setVisibility(View.GONE);
                allupdate.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isNetworkAvailable()) {
                            lottieProgressBar.setVisibility(View.GONE);
                            connectionlost.setVisibility(View.VISIBLE);
                            btnretry.setVisibility(View.VISIBLE);
                            allupdate.setVisibility(View.GONE);

                        }
                        lottieProgressBar.setVisibility(View.GONE);
                        allupdate.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieProgressBar.setVisibility(View.VISIBLE);
                lottieProgressBar.playAnimation();
                connectionlost.setVisibility(View.GONE);
                btnretry.setVisibility(View.GONE);
                allupdate.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isNetworkAvailable()) {
                            lottieProgressBar.setVisibility(View.GONE);
                            connectionlost.setVisibility(View.VISIBLE);
                            btnretry.setVisibility(View.VISIBLE);
                            allupdate.setVisibility(View.GONE);

                        }
                        lottieProgressBar.setVisibility(View.GONE);
                        actionUpdate();
                    }
                }, 2000);
            }
        });
    }


    private void actionUpdate() {
        String judul = et_UpdateJudul.getText().toString();
        String weight = et_Updateweight.getText().toString();
        String height = et_Updateheight.getText().toString();


        if (!judul.isEmpty() && !height.isEmpty() && !weight.isEmpty()) {
            float heightValue = Float.parseFloat(height) / 100;
            float weightValue = Float.parseFloat(weight);

            // Panggil metode untuk memperbarui data menggunakan API
            updateRecordWithAPI(judul, heightValue, weightValue);
        } else {
            if (judul.isEmpty()) {
                et_UpdateJudul.setError("Judul tidak boleh kosong");
            }
            if (height.isEmpty()) {
                et_Updateheight.setError("Tinggi tidak boleh kosong");
            }
            if (weight.isEmpty()) {
                et_Updateweight.setError("Berat tidak boleh kosong");
            }
        }
    }

    private void updateRecordWithAPI(String judul, float weight, float height) {
        ApiService apiService = BmiClient.getClient().create(ApiService.class);
        Call<BmiModel> callBmi = apiService.getBmiNumber(height, weight);
        callBmi.enqueue(new Callback<BmiModel>() {
            @Override
            public void onResponse(Call<BmiModel> call, Response<BmiModel> response) {
                if (response.isSuccessful()) {
                    BmiModel bmiModel = response.body();
                    float bmiValue = bmiModel.getBmi();
                    Call<BmiCategory> callCategory = apiService.getBmiCategory(bmiValue);
                    callCategory.enqueue(new Callback<BmiCategory>() {
                        @Override
                        public void onResponse(Call<BmiCategory> call, Response<BmiCategory> response) {
                            if (response.isSuccessful()) {
                                BmiCategory bmiCategory = response.body();
                                String weightCategory = bmiCategory.getWeightCategory();

                                dbHelper.updateRecord(recordId, judul, height, weight, bmiValue, weightCategory);
                                Toast.makeText(UpdateActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BmiCategory> call, Throwable t) {
                            Toast.makeText(UpdateActivity.this, "Gagal memperbarui kategori berat badan", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<BmiModel> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Gagal menghitung BMI", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadRecordData(int id) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + dbHelper.getTableName() + " WHERE " + dbHelper.getColumnId() + " = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            et_UpdateJudul.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnJudul())));
            et_Updateheight.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnHeight())));
            et_Updateweight.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.getColumnWeight())));
            cursor.close();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Note");
        builder.setMessage("Apakah anda yakin ingin menghapus item ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            dbHelper.deleteRecord(recordId);
            finish();
        });
        builder.setNegativeButton("Tidak", (dialog, which) ->
                dialog.dismiss());
        builder.create().show();
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Batal");
        builder.setMessage("Apakah anda ingin membatalkan perubahan pada form?");

        builder.setPositiveButton("Ya", (dialog, which) -> {
            Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showCancelConfirmationDialog();
    }
}
