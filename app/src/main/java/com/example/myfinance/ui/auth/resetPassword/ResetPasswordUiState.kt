package com.example.myfinance.ui.auth.resetPassword

data class ResetPasswordUiState(
    val email: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalError: String? = null
)