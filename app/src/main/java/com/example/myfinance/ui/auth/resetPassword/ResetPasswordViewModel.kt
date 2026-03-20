package com.example.myfinance.ui.auth.resetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.AuthRepository
import com.example.myfinance.ui.auth.Validate
import com.example.myfinance.ui.auth.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    val repository: AuthRepository, private val validate: Validate
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onLoginChange(newValue: String) {
        when (val result = validate.validateEmail(newValue)) {
            is ValidationResult.Success -> {
                _uiState.update { it.copy(email = newValue, emailError = null) }
            }

            is ValidationResult.Error -> {
                _uiState.update { it.copy(email = newValue, emailError = result.message) }
            }
        }
    }

    fun onResetClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        when (val result = validate.validateEmail(currentState.email)) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        generalError = result.message
                    )
                }
            }

            is ValidationResult.Success -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, generalError = null) }
                    val result = repository.resetPassword(currentState.email)
                    _uiState.update { it.copy(isLoading = false) }
                    result.onSuccess { onSuccess() }
                        .onFailure { _uiState.update { it.copy(generalError = "Ошибка сети") } }
                }
            }
        }
    }
}