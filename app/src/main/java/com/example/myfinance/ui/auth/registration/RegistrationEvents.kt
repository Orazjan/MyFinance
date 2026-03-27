package com.example.myfinance.ui.auth.registration

sealed interface RegistrationEvents {
    data object NavigateToMain : RegistrationEvents
    data object NavigateToLogin : RegistrationEvents
    data class ShowSnackBar(val message: String) : RegistrationEvents
}