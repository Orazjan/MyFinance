package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

@Entity(tableName = "Finances")
public class Finances {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Временный ID для Firestore, пока не будет сгенерирован настоящий
    private String firestoreId;

    // Аннотация @PropertyName указывает, что это поле соответствует полю 'category' в Firestore.
    @PropertyName("category")
    @ColumnInfo(name = "financeResult")
    private String financeResult;

    // Аннотация @PropertyName указывает, что это поле соответствует полю 'sum' в Firestore.
    @PropertyName("sum")
    @ColumnInfo(name = "summa")
    private double summa;

    @ColumnInfo(name = "comments")
    private String comments;

    @PropertyName("type")
    @ColumnInfo(name = "operationType")
    private String operationType;

    @ColumnInfo(name = "date")
    private String date;

    @Exclude
    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    // Пустой конструктор, обязателен для корректной десериализации из Firestore.
    public Finances() {
        this.isSynced = false;
        this.firestoreId = null;
    }

    public Finances(String financeResult, double summa, String operationType, String comments, String date) {
        this.date = date;
        this.financeResult = financeResult;
        this.summa = summa;
        this.comments = comments;
        this.operationType = operationType;
        this.isSynced = false;
    }

    // Геттеры и сеттеры
    @PropertyName("type")
    public String getOperationType() {
        return operationType;
    }

    @PropertyName("type")
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @PropertyName("category")
    public String getFinanceResult() {
        return financeResult;
    }

    @PropertyName("category")
    public void setFinanceResult(String financeResult) {
        this.financeResult = financeResult;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PropertyName("sum")
    public double getSumma() {
        return summa;
    }

    @PropertyName("sum")
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

    @Exclude
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
                ", financeResult='" + financeResult + '\'' +
                ", summa=" + summa +
                ", operationType='" + operationType + '\'' +
                ", date='" + date + '\'' +
                ", isSynced=" + isSynced +
                ", firestoreId='" + firestoreId + '\'' +
                '}';
    }
}
