package com.example.myfinance.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Total_amount")
public class TotalAmount {
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "summa")
    private double summa;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    private String firestoreId;

    public TotalAmount() {
        this.id = 1;
        this.isSynced = false;
        this.firestoreId = null;
    }


    public TotalAmount(double amount, double summa) {
        this();
        this.amount = amount;
        this.summa = summa;
    }

    public TotalAmount(int id, double amount, double summa, String firestoreId, boolean isSynced) {
        this.id = id;
        this.amount = amount;
        this.summa = summa;
        this.firestoreId = firestoreId;
        this.isSynced = isSynced;
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

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }
}