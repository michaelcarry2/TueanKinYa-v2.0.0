package com.example.tueankinya.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.tueankinya.model.DrugTime;

import java.util.List;

public class DrugTimeDiffCallback extends DiffUtil.Callback {

    private final List<DrugTime> oldList;
    private final List<DrugTime> newList;

    public DrugTimeDiffCallback(List<DrugTime> oldList, List<DrugTime> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        DrugTime oldItem = oldList.get(oldItemPosition);
        DrugTime newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
