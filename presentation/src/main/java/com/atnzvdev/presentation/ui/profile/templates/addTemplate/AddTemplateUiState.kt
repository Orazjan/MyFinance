package com.atnzvdev.presentation.ui.profile.templates.addTemplate

import com.atnzvdev.domain.model.TypeOfOperation

data class AddTemplateUiState(
    val nameInput: String = "",
    val amountInput: String = "",
    val selectedType: TypeOfOperation = TypeOfOperation.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
