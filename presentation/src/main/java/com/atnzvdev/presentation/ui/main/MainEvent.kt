package com.atnzvdev.presentation.ui.main

sealed interface MainEvent {
    data object NavigateToAddTransAction : MainEvent
    data class ShowSnackbar(val message: String) : MainEvent
}