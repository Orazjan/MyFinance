package com.atnzvdev.presentation.ui.auth.login

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = "",
    val isLoading: Boolean = false,
)