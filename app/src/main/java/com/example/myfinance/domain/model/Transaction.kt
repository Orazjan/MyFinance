package com.example.myfinance.domain.model

data class Transaction(
    val id: Int = 0,
    val name: String = "",
    val description: String? = "",
    val type: TypeOfOperation = TypeOfOperation.INCOME,
    val amount: Double = 0.0,
    val timeStamp: Long = 0L,
    val receiptUri: String? = null
)
