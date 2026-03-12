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
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var nameUser by mutableStateOf("")
    val emailPattern = Patterns.EMAIL_ADDRESS
    var email by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)
    var nameUserError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var generalError by mutableStateOf<String?>(null)

    fun onChangeUserName(newValue: String) {
        nameUser = newValue
        nameUserError = if (nameUser.isNotEmpty() && nameUser.length <= 2) {
            "Имя не может быть меньше 2х символом"
        } else {
            null
        }
    }

    fun onChangeEmail(newValue: String) {
        email = newValue
        emailError = if (email.isNotEmpty() && !emailPattern.matcher(email).matches()) {
            "Неверный формат почты"
        } else {
            null
        }
    }

    fun onChangePassword(newValue: String) {
        password = newValue
        passwordError = if (password.isNotEmpty() && password.length <= 7) {
            "Пароль должен иметь 8 или больше символов"
        } else null
    }

    fun onRegClick(onSuccess: () -> Unit) {
        generalError = null

        if (emailError != null || passwordError != null || nameUserError != null || password.isEmpty() || email.isEmpty() || nameUser.isEmpty()) {
            generalError = "Пожалуйста, исправьте ошибки в полях"
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = repository.registration(email, password)
            isLoading = false
            result.onSuccess { onSuccess() }.onFailure { exception ->
                generalError = exception.message ?: "Произошла неизвестная ошибка"
            }
        }
    }
}