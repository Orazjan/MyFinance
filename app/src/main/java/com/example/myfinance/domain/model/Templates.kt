package com.example.myfinance.domain.model

data class Templates(
    val id: Int,
    val name: String,
    val isIncome: Boolean,
    val amount: Double,
    val reminderTime: Long,
    val iconRes: Int,
    val order: Int
)
