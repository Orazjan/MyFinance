package com.example.myfinance.domain.model

data class Templates(
    val id: Int,
    val name: String,
    val isIncome: Boolean,
    val amount: Double,
    val reminderTime: Long? = null,
    val iconRes: Int? = null,
    val order: Int = 0
)
