package com.atnzvdev.domain.model

data class Template(
    val id: Long,
    val name: String,
    val isIncome: Boolean,
    val amount: Double,
    val typeOfOperation: TypeOfOperation,
    val reminderTime: Long,
    val iconRes: Int,
    val order: Int
)
