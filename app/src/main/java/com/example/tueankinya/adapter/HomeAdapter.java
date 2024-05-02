package com.example.tueankinya.adapter;

import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tueankinya.R;
import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.utils.DrugTimeDiffCallback;
import com.example.tueankinya.utils.TimeCalculator;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static List<DrugTime> drugTimeList;
    private final DrugDatabaseHelper dbHelper;

    public HomeAdapter(List<DrugTime> drugTimeList, DrugDatabaseHelper dbHelper) {
        HomeAdapter.drugTimeList = drugTimeList;
        this.dbHelper = dbHelper;
    }

    public void updateData(List<DrugTime> updatedList) {
        drugTimeList.clear();
        drugTimeList.addAll(updatedList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView DrugName, AmountDrug, BeforeAfterEat, AlreadyEat, TimeEat, StartDay, EndDay;

        public CheckBox EatActive;
        private HomeAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BeforeAfterEat = itemView.findViewById(R.id.before_after_eat);
            DrugName = itemView.findViewById(R.id.drug_name5446565);
            AlreadyEat = itemView.findViewById(R.id.already_eat);
            TimeEat = itemView.findViewById(R.id.time_eat);
            EatActive = itemView.findViewById(R.id.eat_active);
            StartDay = itemView.findViewById(R.id.start_day);
            EndDay = itemView.findViewById(R.id.end_day);
            AmountDrug = itemView.findViewById(R.id.home_amount_drug);


            EatActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        DrugTime drugTime = drugTimeList.get(position);
                        adapter.updateEatActive(drugTime.getId(), EatActive.isChecked());
                    }
                }
            });
        }
        public void setAdapter(HomeAdapter adapter) {
            this.adapter = adapter;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setAdapter(this);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DrugTime drugTime = drugTimeList.get(position);
        holder.DrugName.setText(drugTime.getDrugName());
        holder.TimeEat.setText(drugTime.getTimeEat());
        holder.StartDay.setText(drugTime.getStartTime());
        holder.EndDay.setText(drugTime.getEndTime());
        holder.AmountDrug.setText("จำนวน " + drugTime.getAmount_drug() + " เม็ด");

        if (holder.EatActive != null) {
            boolean eatActive = dbHelper.getEatActiveForDrug(drugTime.getId());
            holder.EatActive.setChecked(eatActive);

            if (!eatActive) {
                holder.AlreadyEat.setText("ยังไม่กิน");
                holder.AlreadyEat.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_500));
                holder.EatActive.setEnabled(true);
            } else {
                holder.AlreadyEat.setText("กินแล้ว");
                holder.AlreadyEat.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_500));
                holder.EatActive.setEnabled(false);
            }
        }

        if (drugTime.getAfterTime() != null && drugTime.getAfterTime()) {
            holder.BeforeAfterEat.setText("( ก่อนอาหาร )");
        } else if (drugTime.getBeforeTime() != null && drugTime.getBeforeTime()) {
            holder.BeforeAfterEat.setText("( หลังอาหาร )");
        } else {
            holder.BeforeAfterEat.setText("( หลังอาหาร )");
        }
    }

    @Override
    public int getItemCount() {
        return drugTimeList.size();
    }

    private void updateEatActive(long drugId, boolean eatActive) {
        dbHelper.updateEatActive(drugId, eatActive);
        int position = findDrugPosition(drugId);
        if (position != -1) {
            DrugTime drugTime = drugTimeList.get(position);
            drugTime.setEatActive(eatActive);
            notifyItemChanged(position);
        }
    }

    private int findDrugPosition(long drugId) {
        for (int i = 0; i < drugTimeList.size(); i++) {
            if (drugTimeList.get(i).getId() == drugId) {
                return i;
            }
        }
        return -1;
    }

    public void setData(List<DrugTime> newDrugTimeList) {
        DrugTimeDiffCallback diffCallback = new DrugTimeDiffCallback(drugTimeList, newDrugTimeList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        drugTimeList.clear();
        drugTimeList.addAll(newDrugTimeList);

        diffResult.dispatchUpdatesTo(this);
    }
}
