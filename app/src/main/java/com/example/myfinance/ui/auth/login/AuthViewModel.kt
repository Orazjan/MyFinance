package com.example.myfinance.ui.auth.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var isLoading by mutableStateOf(false)

    fun onEmailChanged(newValue: String) {
        _uiState.update {
            it.copy(
                email = newValue,
                emailError = if (newValue.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newValue)
                        .matches()
                ) {
                    "Некорректный формат почты"
                } else null
            )
        }

    }

    fun onPasswordChanged(newValue: String) {
        _uiState.update {
            it.copy(
                password = newValue,
                passwordError = if (newValue.isNotEmpty() && newValue.length < 6) {
                    "Пароль слишком короткий"
                } else null
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.email.isEmpty() || currentState.emailError != null) {
            _uiState.update {
                it.copy(
                    generalError = "Проверьте правильность почты"
                )
            }
            return
        }

        if (currentState.password.isEmpty() || currentState.passwordError != null) {
            _uiState.update {
                it.copy(
                    generalError = "Проверьте правильность почты"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, generalError = null
                )
            }
            val result = repository.login(currentState.email, currentState.password)
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        generalError = exception.message ?: "Произошла неизвестная ошибка"
                    )
                }
            }
        }
    }
}