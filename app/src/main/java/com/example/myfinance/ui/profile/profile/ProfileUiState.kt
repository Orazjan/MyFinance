package com.example.myfinance.ui.profile.profile

data class ProfileUiState(
    val userName: String? = "Гость",
    val email: String? = "Войдите чтобы данные сохранить",
    val plan: String? = null,
    val textForAuthButton: String? = "Войти",
    val isAuth: Boolean? = null
)