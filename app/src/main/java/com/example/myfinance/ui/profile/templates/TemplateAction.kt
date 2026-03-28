package com.example.myfinance.ui.profile.templates

sealed interface TemplateAction {
    data object OnAddTransactionClick : TemplateAction
    data object OnBackClick : TemplateAction
    data object OnDeleteClick : TemplateAction
    data object OnChangeClick : TemplateAction
}