package com.example.myfinance.ui.auth.registration

data class RegistrationUiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val userNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = "",
)