package com.example.myfinance.ui.profile.templates.addTemplate

import com.example.myfinance.domain.model.TypeOfOperation

sealed interface AddTemplateEvent {
    data class OnNameChanged(val name: String) : AddTemplateEvent
    data class OnAmountChanged(val amount: String) : AddTemplateEvent
    data class OnTypeChanged(val type: TypeOfOperation) : AddTemplateEvent
    data class ShowSnackbar(val message: String) : AddTemplateEvent

}