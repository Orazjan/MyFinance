package com.atnzvdev.presentation.ui.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atnzvdev.domain.repository.AuthRepository
import com.atnzvdev.presentation.ui.auth.Validate
import com.atnzvdev.presentation.ui.auth.ValidationResult
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
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository, private val validate: Validate
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegistrationEvents>()
    val events = _events.asSharedFlow()

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

    fun onLoginClick() {
        viewModelScope.launch {
            _events.emit(RegistrationEvents.NavigateToLogin)
        }
    }

    fun onRegClick() {
        val currentState = _uiState.value
        if (currentState.isLoading) return
        val validationResult = validate.validateRegistration(
            currentState.email, currentState.password, currentState.userName
        )
        when (validationResult) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        generalError = validationResult.message
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

                    val resultOfRegistration =
                        repository.registration(currentState.email, currentState.password)
                    _uiState.update { it.copy(isLoading = false) }

                    resultOfRegistration.onSuccess { _events.emit(RegistrationEvents.NavigateToMain) }
                        .onFailure { exception ->
                            _events.emit(
                                RegistrationEvents.ShowSnackBar(
                                    exception.message ?: "Ошибка регистрации"
                                )
                            )
                        }


                }
            }
        }
    }
}