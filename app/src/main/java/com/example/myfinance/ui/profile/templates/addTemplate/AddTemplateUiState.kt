package com.example.myfinance.ui.profile.templates.addTemplate

import com.example.myfinance.domain.model.TypeOfOperation

data class AddTemplateUiState(
    val nameInput: String = "",
    val amountInput: String = "",
    val selectedType: TypeOfOperation = TypeOfOperation.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
