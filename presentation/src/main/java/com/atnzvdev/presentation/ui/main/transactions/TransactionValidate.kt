package com.atnzvdev.presentation.ui.main.transactions

import com.atnzvdev.domain.model.Transaction
import com.atnzvdev.domain.model.TypeOfOperation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionValidate @Inject constructor() {
    fun validateType(type: TypeOfOperation): ValidationResult {
        return if (type.equals(TypeOfOperation.ALL) && type.equals(TypeOfOperation.fromDisplayName("Общее"))) {
            ValidationResult.Error("Выберите Доход или Расход")
        } else ValidationResult.Success

    }

    fun validateAmount(amount: String): ValidationResult {
        return if (amount.isEmpty()) ValidationResult.Error("Сумма не может быть пустой")
        else ValidationResult.Success
    }

    fun validateName(name: String): ValidationResult {
        return if (name.isEmpty()) ValidationResult.Error("Название не может быть пустым")
        else ValidationResult.Success
    }

    fun validateTransaction(transaction: Transaction): ValidationResult {
        val nameResult = validateName(transaction.title)
        if (nameResult is ValidationResult.Error) return nameResult

        val amountResult = validateAmount(transaction.amount.toString())
        if (amountResult is ValidationResult.Error) return amountResult

        val typeResult = validateType(transaction.type)
        if (typeResult is ValidationResult.Error) return typeResult

        return ValidationResult.Success
    }

}
