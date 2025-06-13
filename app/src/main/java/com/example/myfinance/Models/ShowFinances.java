package com.example.myfinance.Models;

import androidx.annotation.NonNull;

public class ShowFinances {
    private int id;
    private double sum;
    private String name;
    private String comments;

    public ShowFinances() {
    }

    public ShowFinances(int id, double sum, String name, String comments) {
        this.id = id;
        this.sum = sum;
        this.name = name;
        this.comments = comments;
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
