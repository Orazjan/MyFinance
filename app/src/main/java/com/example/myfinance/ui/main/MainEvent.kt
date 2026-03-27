package com.example.myfinance.ui.main

sealed interface MainEvent {
    data object NavigateToAddTransAction : MainEvent
    data object NavigateBack : MainEvent
    data class ShowSnackbar(val message: String) : MainEvent
}