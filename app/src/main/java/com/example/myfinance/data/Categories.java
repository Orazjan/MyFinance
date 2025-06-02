package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "finance_table")
public class Categories {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "categoryName")
    private String categoryName;

    @ColumnInfo(name = "sum")
    private double sum;

    public Categories(String categoryName, double sum) {
        this.categoryName = categoryName;
        this.sum = sum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    @NonNull
    @Override
    public String toString() {
        return "Categories{" +
                "categoryName='" + categoryName + '\'' +
                ", id=" + id +
                ", sum=" + sum +
                '}';
    }
}
