package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class DateSum {
    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "total")
    public double total;

    public DateSum(String date, double total) {
        this.date = date;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public double getTotal() {
        return total;
    }

    @NonNull
    @Override
    public String toString() {
        return "DateSum{" +
                "date='" + date + '\'' +
                ", total=" + total +
                '}';
    }
}