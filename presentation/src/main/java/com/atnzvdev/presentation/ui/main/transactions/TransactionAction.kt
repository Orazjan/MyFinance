package com.atnzvdev.presentation.ui.main.transactions

sealed interface TransactionAction {
    data class ShowSnackbar(val message: String) : TransactionAction
    data object NavigateBack : TransactionAction
}