package com.example.myfinance.ui.profile.templates

sealed interface TemplateEvent {
    data object NavigateBack : TemplateEvent
    data object NavigateToAddTemplate : TemplateEvent
    data class ShowSnackbar(val message: String) : TemplateEvent
}