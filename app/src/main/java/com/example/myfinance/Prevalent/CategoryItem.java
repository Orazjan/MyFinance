package com.example.myfinance.Prevalent;

public class CategoryItem {
    private String CategoryName;
    private double sum;
    private String operation;

    public CategoryItem() {
    }

    public CategoryItem(String categoryName, double sum, String operation) {
        CategoryName = categoryName;
        this.sum = sum;
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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
