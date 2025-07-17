package com.example.myfinance.Models;

import androidx.annotation.NonNull;

public class ShowFinances {
    private int id; // Room ID
    private double sum;
    private String name; // Category
    private String comments;
    private String date;
    private String firestoreId;
    private String operationType;

    public ShowFinances() {
    }

    public ShowFinances(int id, double sum, String name, String operationType, String comments, String date, String firestoreId) {
        this.id = id;
        this.sum = sum;
        this.name = name;
        this.comments = comments;
        this.date = date;
        this.operationType = operationType;
        this.firestoreId = firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSum() {
        return sum;
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

    @NonNull
    @Override
    public String toString() {
        return "ShowFinances{" +
                "comments='" + comments + '\'' +
                ", date='" + date + '\'' +
                ", firestoreId='" + firestoreId + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", operationType='" + operationType + '\'' +
                ", sum=" + sum +
                '}';
    }
}
