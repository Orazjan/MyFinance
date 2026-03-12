package com.example.myfinance.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onChangeUserName(newValue: String) {
        _uiState.update {
            it.copy(
                userName = newValue,
                userNameError = if (newValue.isNotEmpty() && newValue.length <= 2) {
                    "Имя не может быть меньше 2х символом"
                } else {
                    null
                }
            )
        }

    }

    fun onChangeEmail(newValue: String) {
        _uiState.update {
            it.copy(
                email = newValue,
                emailError = if (newValue.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newValue)
                        .matches()
                ) {
                    "Неверный формат почты"
                } else {
                    null
                }
            )
        }

    }

    fun onChangePassword(newValue: String) {
        _uiState.update {
            it.copy(
                password = newValue,
                passwordError = if (newValue.isNotEmpty() && newValue.length <= 7) {
                    "Пароль должен иметь 8 или больше символов"
                } else null


            )
        }

    }

    fun onRegClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.email.isEmpty() || currentState.emailError != null) {
            _uiState.update {
                it.copy(
                    generalError = "Проверьте данные почты"
                )
            }
            return
        }
        if (currentState.userName.isEmpty() || currentState.userNameError != null) {
            _uiState.update {
                it.copy(
                    generalError = "Проверьте данные имени"
                )
            }
            return
        }
        if (currentState.password.isEmpty() || currentState.passwordError != null) {
            _uiState.update {
                it.copy(
                    generalError = "Проверьте данные пароля"
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
            val result = repository.registration(currentState.email, currentState.password)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { onSuccess() }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        generalError = exception.message ?: "Произошла неизвестная ошибка"
                    )
                }
            }
        }
    }
}