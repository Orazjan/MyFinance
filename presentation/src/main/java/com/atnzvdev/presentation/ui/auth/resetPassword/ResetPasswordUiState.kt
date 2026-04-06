package com.atnzvdev.presentation.ui.auth.resetPassword

data class ResetPasswordUiState(
    val email: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalError: String? = null
)