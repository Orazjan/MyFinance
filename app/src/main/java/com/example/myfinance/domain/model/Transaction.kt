package com.example.myfinance.domain.model

data class Transaction(
    val id: Long = 0,
    val title: String,
    val description: String,
    val amount: Double,
    val type: TypeOfOperation,
    val date: Long
)
