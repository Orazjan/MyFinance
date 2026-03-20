package com.example.myfinance.ui.profile.templates

import com.example.myfinance.domain.model.TypeOfOperation

sealed interface TemplateEvent {
    data class OnNameChanged(val name: String) : TemplateEvent
    data class OnAmountChanged(val amount: String) : TemplateEvent
    data class OnTypeChanged(val type: TypeOfOperation) : TemplateEvent

    object OnSaveClick : TemplateEvent
    object OnLoad : TemplateEvent
}