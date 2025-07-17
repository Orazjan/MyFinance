package com.example.myfinance.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories_table")
public class Categories {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "categoryName")
    private String categoryName;

    @ColumnInfo(name = "sum")
    private double sum;

    @ColumnInfo(name = "operationType")
    private String operationType;

    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    private String firestoreId;

    public Categories() {
        this.isSynced = false;
        this.firestoreId = null;
    }

    public Categories(String categoryName, double sum) {
        this();
        this.categoryName = categoryName;
        this.sum = sum;
    }

    public Categories(String categoryName, double sum, String operationType) {
        this(categoryName, sum);
        this.categoryName = categoryName;
        this.sum = sum;
        this.operationType = operationType;
    }

    public Categories(String categoryName, double sum, String operationType, String firestoreId, boolean isSynced) {
        this(categoryName, sum);
        this.firestoreId = firestoreId;
        this.isSynced = isSynced;
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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

    @NonNull
    @Override
    public String toString() {
        return "Categories{" +
                "categoryName='" + categoryName + '\'' +
                ", id=" + id +
                ", sum=" + sum +
                ", operationType='" + operationType + '\'' +
                ", isSynced=" + isSynced +
                ", firestoreId='" + firestoreId + '\'' +
                '}';
    }
}
    