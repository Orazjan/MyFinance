package com.example.myfinance.ui.main.transActions

import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation

data class AddTransActionUiState(
    val templates: List<Templates> = emptyList(),
    val selectedTemplate: Templates? = null,

    val nameInput: String = "",
    val amountInput: String = "",
    val description: String = "",

    val typeOfOperation: TypeOfOperation = TypeOfOperation.entries[0],
    val selectedIndex: Int = 0
)
