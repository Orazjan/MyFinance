package com.example.myfinance.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    var uiState: StateFlow<MainUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            isUserAuth()

        }
    }

    suspend fun isUserAuth() {
        authRepository.getCurrentUser()

    }
}