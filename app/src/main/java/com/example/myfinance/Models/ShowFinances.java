package com.example.myfinance.Models;

public class ShowFinances {
    private int id;
    private double sum;
    private String name;

    public ShowFinances() {
    }

    public ShowFinances(int id, double sum, String name) {
        this.id = id;
        this.sum = sum;
        this.name = name;
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

    @Override
    public String toString() {
        return "ShowFinances{" +
                "id=" + id +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                '}';
    }
}
