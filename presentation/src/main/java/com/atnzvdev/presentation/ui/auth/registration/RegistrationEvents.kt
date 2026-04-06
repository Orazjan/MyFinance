package com.atnzvdev.presentation.ui.auth.registration

sealed interface RegistrationEvents {
    data object NavigateToMain : RegistrationEvents
    data object NavigateToLogin : RegistrationEvents
    data class ShowSnackBar(val message: String) : RegistrationEvents
}