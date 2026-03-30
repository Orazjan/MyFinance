package com.example.myfinance.ui.main.transactions

sealed interface TransactionAction {
    data class ShowSnackbar(val message: String) : TransactionAction
    data object NavigateBack : TransactionAction
}