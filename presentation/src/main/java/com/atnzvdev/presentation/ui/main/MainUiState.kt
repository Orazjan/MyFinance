package com.atnzvdev.presentation.ui.main

import com.atnzvdev.domain.model.Months
import com.atnzvdev.domain.model.Transaction
import com.atnzvdev.domain.model.TypeOfOperation

data class MainUiState(
    val totalBalance: String = "0.0",
    val totalExpense: String = "0.0",
    val transactions: List<Transaction> = emptyList(),
    val selectedType: TypeOfOperation = TypeOfOperation.ALL,
    val selectedMonth: Months = Months.ALL_PERIOD,
    val isLoading: Boolean = false,
    val isSyncEnabled: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
    val selectedTransaction: Transaction? = null
)