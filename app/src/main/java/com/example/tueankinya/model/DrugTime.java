package com.example.tueankinya.model;

import java.util.ArrayList;
import java.util.List;

public class DrugTime {
    private int id;
    private String drug_name, amount_drug;
    private String time_eat;
    private String start_time, end_time;
    private Boolean after_time, before_time, eat_active;

    public DrugTime(int id, String drug_name, String amount_drug, String start_time, String end_time, String time_eat, Boolean before_time, Boolean after_time, Boolean eat_active) {
        this.id = id;
        this.drug_name = drug_name;
        this.amount_drug = amount_drug;
        this.start_time = start_time;
        this.end_time = end_time;
        this.time_eat = time_eat;
        this.before_time = before_time;
        this.after_time = after_time;
        this.eat_active = eat_active;
    }

    public DrugTime(int id, String drugName, String amountDrug, String timeEat) {
        this.id = id;
        this.drug_name = drugName;
        this.amount_drug = amountDrug;
        this.time_eat = timeEat;
    }

    public DrugTime(int id, String drugNameValue, String startTime, String endTime, String timeEat, Boolean beforeTimeSelected, Boolean afterTimeSelected) {
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrugName() {
        return drug_name;
    }

    public void setDrugName(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }
    public String getAmount_drug() {
        return amount_drug;
    }
    public void setAmount_drug(String amount_drug) {
        this.amount_drug = amount_drug;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public String getTimeEat() {
        return time_eat;
    }

    public void setTimeEat(String time_eat) { this.time_eat = time_eat; }

    public Boolean getEatActive() {
        return eat_active;
    }

    public void setEatActive(Boolean eat_active) {
        this.eat_active = eat_active;
    }

    public Boolean getBeforeTime() {
        return before_time;
    }

    public void setBeforeTime(Boolean before_time) {
        this.before_time = before_time;
    }

    public Boolean getAfterTime() {
        return after_time;
    }

    public void setAfterTime(Boolean after_time) {
        this.after_time = after_time;
    }
}