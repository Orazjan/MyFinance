package com.example.myfinance.Models;

import androidx.annotation.NonNull;

public class ShowFinances {
    private int id; // Room ID
    private double sum;
    private String name; // Category
    private String comments;
    private String date;
    private String firestoreId;

    public ShowFinances() {
    }

    public ShowFinances(int id, double sum, String name, String comments, String date, String firestoreId) {
        this.id = id;
        this.sum = sum;
        this.name = name;
        this.comments = comments;
        this.date = date;
        this.firestoreId = firestoreId;
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
                ", id=" + id +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                '}';
    }
}
