package com.example.tueankinya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tueankinya.R;
import com.example.tueankinya.dao.TakingMedicineDatabaseHelper;
import com.example.tueankinya.model.TakingMedicine;

import java.util.List;

public class HistoryDrugAdapter extends RecyclerView.Adapter<HistoryDrugAdapter.ViewHolder> {
    private List<TakingMedicine> takingMedicineList;
    private Context context;
    private TakingMedicineDatabaseHelper dbHelper;

    public HistoryDrugAdapter(List<TakingMedicine> takingMedicineList, Context context) {
        this.takingMedicineList = takingMedicineList;
        this.context = context;
        this.dbHelper = new TakingMedicineDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_drug_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TakingMedicine takingMedicine = takingMedicineList.get(position);
        holder.textHistoryDrug.setText(takingMedicine.getCreateAt());
        holder.nameDrugHistory.setText(takingMedicine.getDrugName());
        holder.timeDrugHistory.setText(takingMedicine.getTimeEat());

        if (takingMedicine.getEatActive()) {
            holder.statusDrugHistory.setText("กินแล้ว");
            holder.statusDrugHistory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_500));
        } else {
            holder.statusDrugHistory.setText("ไม่ได้กิน");
            holder.statusDrugHistory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_500));
        }
    }

    @Override
    public int getItemCount() {
        return takingMedicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private TextView textHistoryDrug, nameDrugHistory, timeDrugHistory, statusDrugHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textHistoryDrug = itemView.findViewById(R.id.text_hitory_drug);
            nameDrugHistory = itemView.findViewById(R.id.name_drug_history);
            timeDrugHistory = itemView.findViewById(R.id.time_drug_history);
            statusDrugHistory = itemView.findViewById(R.id.status_drug_history);
        }
    }
}
