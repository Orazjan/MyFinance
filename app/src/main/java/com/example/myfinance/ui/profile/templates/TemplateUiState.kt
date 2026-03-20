package com.example.myfinance.ui.profile.templates

import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation

data class TemplateUiState(
    val items: List<Templates> = emptyList(),
    val nameInput: String = "",
    val amountInput: String = "",
    val selectedType: TypeOfOperation = TypeOfOperation.EXPENSES,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


