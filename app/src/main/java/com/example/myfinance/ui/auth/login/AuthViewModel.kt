package com.example.myfinance.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.AuthRepository
import com.example.myfinance.ui.auth.Validate
import com.example.myfinance.ui.auth.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository, private val validate: Validate
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvents>()
    val events = _events.asSharedFlow()

    fun onEmailChanged(newValue: String) {
        when (val result = validate.validateEmail(newValue)) {
            is ValidationResult.Success -> {
                _uiState.update { it.copy(email = newValue, emailError = null) }
            }

            is ValidationResult.Error -> {
                _uiState.update { it.copy(email = newValue, emailError = result.message) }
            }
        }

    }

    fun onPasswordChanged(newValue: String) {
        when (val result = validate.validatePassword(newValue)) {
            is ValidationResult.Success -> {
                _uiState.update {
                    it.copy(
                        password = newValue, passwordError = null
                    )
                }
            }

            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        password = newValue, passwordError = result.message
                    )
                }
            }
        }
    }

    fun resetPasswordClick() {
        viewModelScope.launch {
            _events.emit(AuthEvents.NavigateToResetPassword)
        }
    }

    fun registrationClick() {
        viewModelScope.launch {
            _events.emit(AuthEvents.NavigateToRegistration)
        }
    }

    fun login() {
        if (_uiState.value.isLoading) return
        val currentState = _uiState.value
        when (val result = validate.validateAuth(currentState.email, currentState.password)) {
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
                    val resultOfAuth = repository.login(currentState.email, currentState.password)
                    _uiState.update { it.copy(isLoading = false) }
                    resultOfAuth.onSuccess { _events.emit(AuthEvents.NavigateToMain) }
                        .onFailure { ex ->
                            _uiState.update { it.copy(generalError = ex.message ?: "Ошибка входа") }
                        }
                }
            }
        }
    }
}