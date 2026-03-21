package com.example.myfinance.ui.main.transActions

import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation

sealed interface TransActionEvent {
    data class OnNameChanged(val newName: String) : TransActionEvent
    data class OnAmountChanged(val newAmount: String) : TransActionEvent
    data class OnTypeChanged(val newType: TypeOfOperation) : TransActionEvent
    data class OnDescriptionChanged(val newDescription: String) : TransActionEvent
    data class OnCategorySelected(val index: Int) : TransActionEvent
    class OnSaveClicked(val onSuccess: () -> Unit) : TransActionEvent
    data class OnTemplateSelected(val template: Templates): TransActionEvent

    object DowloadCategories : TransActionEvent
}