package com.example.myfinance.ui.profile

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
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    var uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                auth.getCurrentUser().fold(onSuccess = { user ->
                    if (user != null) {
                        currentState.copy(
                            userName = user.displayName ?: "Гость",
                            email = user.email,
                            textForAuthButton = "Выход",
                            plan = "* Бесплатный план",
                            isAuth = true
                        )
                    } else {
                        currentState.copy(
                            userName = "Гость",
                            isAuth = false,
                            textForAuthButton = "Войти",
                            plan = "* Гостевой режим"
                        )
                    }
                }, onFailure = { error -> currentState.copy(isAuth = false) })
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            auth.logOut()
            _uiState.update {
                it.copy(
                    userName = "Гость",
                    email = "Войдите чтобы сохранить данные",
                    isAuth = false,
                    textForAuthButton = "Войти",
                    plan = "* Гостевой режим"
                )
            }
        }
    }
}