package com.example.myfinance.ui.main.transactions

import com.example.myfinance.domain.model.Template
import com.example.myfinance.domain.model.TypeOfOperation

sealed interface TransactionEvent {
    data class OnNameChanged(val newName: String) : TransactionEvent
    data class OnAmountChanged(val newAmount: String) : TransactionEvent
    data class OnTypeChanged(val newType: TypeOfOperation) : TransactionEvent
    data class OnDescriptionChanged(val newDescription: String) : TransactionEvent
    data class OnCategorySelected(val index: Int) : TransactionEvent
    data object OnSaveClicked : TransactionEvent
    data class OnTemplateSelected(val template: Template): TransactionEvent
    object DowloadCategories : TransactionEvent
}