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
class ResetPasswordViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onLoginChange(newValue: String) {
        _uiState.update {
            it.copy(
                email = newValue,
                emailError = if (newValue.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newValue)
                        .matches()
                ) {
                    "Неверный формат почты"
                } else null
            )
        }
    }

    fun onResetClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.email.isEmpty() || currentState.emailError != null) {
            _uiState.update { it.copy(generalError = "Проверьте правильность почты") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val result = repository.resetPassword(currentState.email)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { onSuccess() }
                .onFailure { _uiState.update { it.copy(generalError = "Ошибка сети") } }
        }


    }
}