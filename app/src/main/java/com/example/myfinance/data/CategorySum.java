package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class CategorySum {
    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "total")
    public double total;

    public CategorySum(String category, double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public double getTotal() {
        return total;
    }

    @NonNull
    @Override
    public String toString() {
        return "CategorySum{" +
                "category='" + category + '\'' +
                ", total=" + total +
                '}';
    }
}