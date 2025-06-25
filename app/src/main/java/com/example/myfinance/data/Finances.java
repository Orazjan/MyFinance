package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Finances")
public class Finances {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "Finance result")
    private String FinanceResult;

    @ColumnInfo(name = "suma")
    private double summa;

    @ColumnInfo(name = "comments")
    private String comments;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    private String firestoreId;

    public Finances() {
        this.isSynced = false;
        this.firestoreId = null;
    }

    public Finances(String financeResult, double summa, String comments, String date) {
        this.date = date;
        this.FinanceResult = financeResult;
        this.summa = summa;
        this.comments = comments;
        this.isSynced = false;
    }

    public Finances(String firestoreId, String financeResult, double summa, String comments, String date) {
        this.firestoreId = firestoreId;
        this.date = date;
        this.FinanceResult = financeResult;
        this.summa = summa;
        this.comments = comments;
        this.isSynced = true;
    }

    public String getFinanceResult() {
        return FinanceResult;
    }

    public void setFinanceResult(String financeResult) {
        FinanceResult = financeResult;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @NonNull
    @Override
    public String toString() {
        return "Finances{" +
                "comments='" + comments + '\'' +
                ", id=" + id +
                ", FinanceResult='" + FinanceResult + '\'' +
                ", summa=" + summa +
                ", date='" + date + '\'' +
                ", firestoreId='" + firestoreId + '\'' +
                '}';
    }
}
