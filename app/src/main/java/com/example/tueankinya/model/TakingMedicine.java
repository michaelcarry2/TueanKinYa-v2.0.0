package com.example.tueankinya.model;

public class TakingMedicine {
    private int id;
    private String drug_name, time_eat, create_at;
    private Boolean eat_active;

    public TakingMedicine(String drug_name, String time_eat, Boolean eat_active, String create_at) {
        this.drug_name = drug_name;
        this.time_eat = time_eat;
        this.eat_active = eat_active;
        this.create_at = create_at;
    }

    public TakingMedicine() {
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

    public String getTimeEat() {
        return time_eat;
    }

    public void setTimeEat(String time_eat) {
        this.time_eat = time_eat;
    }

    public Boolean getEatActive() {
        return eat_active;
    }

    public void setEatActive(Boolean eat_active) {
        this.eat_active = eat_active;
    }

    public String getCreateAt() {
        return create_at;
    }

    public void setCreateAt(String create_at) {
        this.create_at = create_at;
    }
}