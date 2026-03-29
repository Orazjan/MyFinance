package com.example.myfinance.ui.main.transactions

import com.example.myfinance.domain.model.Template
import com.example.myfinance.domain.model.TypeOfOperation

data class AddTransActionUiState(
    val templates: List<Template> = emptyList(),
    val selectedTemplate: Template? = null,

    val nameInput: String = "",
    val amountInput: String = "",
    val description: String = "",

    val nameError: String? = null,
    val amountError: String? = null,

    val typeOfOperation: TypeOfOperation = TypeOfOperation.INCOME,
    val selectedIndex: Int = 0,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
