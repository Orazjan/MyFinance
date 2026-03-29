package com.example.myfinance.domain.model

data class FinanceSummary(
    val baseBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val currentBalance: Double = 0.0,
)
