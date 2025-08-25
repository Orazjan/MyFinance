package com.example.myfinance.Adapters;

import java.io.Serializable;

/**
 * Класс-модель для хранения сводных данных по категории.
 * Реализует Serializable для передачи через Bundle между фрагментами.
 */
public class CategorySummary implements Serializable {
    private String categoryName;
    private double totalSum;

    public CategorySummary(String categoryName, double totalSum) {
        this.categoryName = categoryName;
        this.totalSum = totalSum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getTotalSum() {
        return totalSum;
    }
}
