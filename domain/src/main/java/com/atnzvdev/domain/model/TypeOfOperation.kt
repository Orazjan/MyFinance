package com.atnzvdev.domain.model

enum class TypeOfOperation(val nameOfType: String) {
    ALL("Общее"),
    INCOME("Доходы"),
    EXPENSES("Расходы");

    companion object {
        fun fromDisplayName(displayName: String): TypeOfOperation? {
            return entries.find { it.nameOfType.equals(displayName, ignoreCase = true) }
        }
        fun fromString(value: String?): TypeOfOperation? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}