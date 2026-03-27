package com.example.myfinance.domain.model

enum class TypeOfOperation(val nameOfType: String) {
    ALL("Общее"),
    INCOME("Доходы"),
    EXPENSES("Расходы");

    companion object {
        fun fromString(value: String?): TypeOfOperation? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}