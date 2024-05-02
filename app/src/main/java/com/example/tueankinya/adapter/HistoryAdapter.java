package com.example.tueankinya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tueankinya.R;
import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.utils.TimeCalculator;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<DrugTime> drugTimeList;
    private final Context context;
    private final DrugDatabaseHelper dbHelper;

    public HistoryAdapter(List<DrugTime> drugTimeList, Context context) {
        this.drugTimeList = drugTimeList;
        this.context = context;
        this.dbHelper = new DrugDatabaseHelper(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView drugNameTextView;
        ImageView deleteDrug;
        TextView calculateDayTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drugNameTextView = itemView.findViewById(R.id.drug_name2);
            calculateDayTextView = itemView.findViewById(R.id.calculate_day);
            deleteDrug = itemView.findViewById(R.id.delete_drug);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drug_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DrugTime drugTime = drugTimeList.get(position);
        holder.drugNameTextView.setText(drugTime.getDrugName());
        long daysDifference = TimeCalculator.calculateDays(drugTime);
        if (daysDifference != -1) {
            String schedule = "เป็นเวลา " + daysDifference + " วัน";
            holder.calculateDayTextView.setText(schedule);
        } else {
            holder.calculateDayTextView.setText("ไม่สามารถคำนวณวันได้");
        }

        holder.deleteDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(position);
            }
        });
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ยืนยันการลบ");
        builder.setMessage("คุณต้องการลบรายการนี้หรือไม่?");
        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItemFromDatabase(position);
                drugTimeList.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("ไม่", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItemFromDatabase(int position) {
        int drugIdToDelete = (int) drugTimeList.get(position).getId();
        dbHelper.deleteDrug(drugIdToDelete);
    }

    @Override
    public int getItemCount() {
        return drugTimeList.size();
    }
}