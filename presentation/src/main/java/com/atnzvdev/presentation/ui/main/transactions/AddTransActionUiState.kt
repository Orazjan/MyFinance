package com.atnzvdev.presentation.ui.main.transactions

import com.atnzvdev.domain.model.Template
import com.atnzvdev.domain.model.TypeOfOperation

data class AddTransActionUiState(
    val templates: List<Template> = emptyList(),
    val selectedTemplate: Template? = null,

    val nameInput: String = "",
    val amountInput: String = "",
    val description: String = "",

    val nameError: String? = null,
    val amountError: String? = null,
    val typeError: String? = null,
    val generalError: String? = null,

    val typeOfOperation: TypeOfOperation = TypeOfOperation.INCOME,
    val selectedIndex: Int = 0,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
