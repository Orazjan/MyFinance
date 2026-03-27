package com.example.myfinance.domain.model

data class Transactions(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val type: TypeOfOperation,
    val amount: Double,
    val timeStamp: Long,
    val receiptUri: String? = null
)
