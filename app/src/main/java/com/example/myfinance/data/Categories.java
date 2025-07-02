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

    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    private String firestoreId;

    // Конструктор по умолчанию для Room
    public Categories() {
        this.isSynced = false;
        this.firestoreId = null;
    }

    public Categories(String categoryName, double sum) {
        this();
        this.categoryName = categoryName;
        this.sum = sum;
    }

    public Categories(String categoryName, double sum, String firestoreId, boolean isSynced) {
        this(categoryName, sum);
        this.firestoreId = firestoreId;
        this.isSynced = isSynced;
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
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", sum=" + sum +
                ", isSynced=" + isSynced +
                ", firestoreId='" + firestoreId + '\'' +
                '}';
    }
}
