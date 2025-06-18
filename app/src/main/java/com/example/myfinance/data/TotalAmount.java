package com.example.myfinance.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Total_amount")
public class TotalAmount {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "summa")
    private double summa;

    @ColumnInfo(name = "amount")
    private double amount;

    public TotalAmount(double amount, double summa) {
        this.amount = amount;
        this.summa = summa;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }


    public double getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
