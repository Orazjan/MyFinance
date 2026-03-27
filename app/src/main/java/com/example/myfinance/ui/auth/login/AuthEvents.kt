package com.example.myfinance.ui.auth.login

sealed interface AuthEvents {
    data object NavigateToRegistration : AuthEvents
    data object NavigateToResetPassword : AuthEvents
    data object NavigateToMain : AuthEvents
    data class ShowSnackBar(val message: String) : AuthEvents
}