package com.example.myfinance.Models;

import java.io.Serializable;

/**
 * Класс-модель, представляющий одну финансовую транзакцию.
 * Реализует интерфейс Serializable, чтобы объекты этого класса
 * можно было передавать через Bundle в другой фрагмент.
 */
public class Transaction implements Serializable {

    // Имя категории транзакции.
    private final String category;
    // Сумма транзакции.
    private final double amount;

    /**
     * Конструктор для создания новой транзакции.
     *
     * @param category Имя категории транзакции.
     * @param amount Сумма транзакции.
     */
    public Transaction(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    /**
     * Возвращает имя категории.
     *
     * @return Имя категории.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Возвращает сумму транзакции.
     *
     * @return Сумма транзакции.
     */
    public double getAmount() {
        return amount;
    }
}
