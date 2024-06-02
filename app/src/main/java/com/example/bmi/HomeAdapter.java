package com.example.bmi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bmi.activity.UpdateActivity;
import com.example.bmi.models.BmiModel;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<BmiModel> bmiModelList;

    public HomeAdapter(Context context, List<BmiModel> bmiModelList) {
        this.context = context;
        this.bmiModelList = bmiModelList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        BmiModel bmi = bmiModelList.get(position);
        holder.tvTitle.setText(bmi.getJudul());
        holder.tvWeight.setText("Weight: " + bmi.getWeight() + " kg");
        holder.tvHeight.setText("Height: " + bmi.getHeight() + " M");

        holder.tvResult.setText(String.format("BMI: %.2f", bmi.getBmi()));
        holder.tvResultCategory.setText("Category: " + bmi.getWeightCategory());

        String timestampText;
        if (bmi.getCreatedAt().equals(bmi.getUpdatedAt())) {
            timestampText = "Created at " + bmi.getCreatedAt();
        } else {
            timestampText = "Updated at " + bmi.getUpdatedAt();
        }
        holder.tvTimestamp.setText(timestampText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("record_id", bmi.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bmiModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvWeight, tvHeight, tvTimestamp, tvResult, tvResultCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvWeight = itemView.findViewById(R.id.tv_Weight);
            tvHeight = itemView.findViewById(R.id.tv_height);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvResultCategory = itemView.findViewById(R.id.tv_resultCategory);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}
