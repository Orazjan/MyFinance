package com.example.myfinance.ui.auth.registration

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
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository, private val validate: Validate
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onChangeUserName(newValue: String) {

        when (val result = validate.validateName(newValue)) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        userName = newValue, userNameError = result.message
                    )
                }
            }

            is ValidationResult.Success -> {
                _uiState.update {
                    it.copy(
                        userName = newValue, userNameError = null
                    )
                }
            }
        }
    }

    fun onChangeEmail(newValue: String) {
        when (val result = validate.validateEmail(newValue)) {
            is ValidationResult.Success -> {
                _uiState.update { it.copy(email = newValue, emailError = null) }
            }

            is ValidationResult.Error -> {
                _uiState.update { it.copy(email = newValue, emailError = result.message) }
            }
        }

    }

    fun onChangePassword(newValue: String) {
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

    fun onRegClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val result = validate.validateRegistration(
            _uiState.value.email,
            _uiState.value.password,
            _uiState.value.userName
        )
        when (result) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        generalError = result.message
                    )
                }
            }

            is ValidationResult.Success -> {
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
    }
}