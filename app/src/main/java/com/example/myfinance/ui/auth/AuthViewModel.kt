package com.example.myfinance.ui.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Данные ввода
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var generalError by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)

    fun onEmailChanged(newValue: String) {
        email = newValue
        emailError =
            if (newValue.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                "Некорректный формат почты"
            } else {
                null
            }
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        passwordError = if (newValue.isNotEmpty() && newValue.length < 6) {
            "Пароль слишком короткий"
        } else {
            null
        }
    }

    fun login(onSuccess: () -> Unit) {
        generalError = null

        if (emailError != null || passwordError != null || email.isEmpty() || password.isEmpty()) {
            generalError = "Пожалуйста, исправьте ошибки в полях"
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = repository.login(email, password)
            isLoading = false

            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                generalError = exception.message ?: "Произошла неизвестная ошибка"
            }
        }
    }
}