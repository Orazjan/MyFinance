package com.example.myfinance.Prevalent;

public class CategoryItem {
    private String CategoryName;
    private double sum;

    public CategoryItem() {
    }

    public CategoryItem(String categoryName, double sum) {
        CategoryName = categoryName;
        this.sum = sum;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
