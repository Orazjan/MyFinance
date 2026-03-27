package com.example.myfinance.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.model.Months
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events = _events.asSharedFlow()

    private var fetchJob: Job? = null
    
    init {
        checkAuthState()
        loadTransactions()
    }

    fun onAction(actions: MainAction) {
        when (actions) {
            is MainAction.OnAddTransactionClick -> navigateToAddTransactions()
            is MainAction.OnBackClick -> navigateToBack()
            is MainAction.OnDeleteClick -> TODO()
            is MainAction.OnDeleteTransactionClick -> TODO()
            is MainAction.OnShowDetailClick -> TODO()
            is MainAction.OnTransactionClick -> TODO()
            is MainAction.OnMonthSelected -> onMonthSelected(actions.month)
            is MainAction.OnTypeSelected -> onTypeSelected(actions.type)

        }
    }

    private fun onDeleteClick() {
        TODO("Not yet implemented")
    }

    private fun onDetailClick() {
        TODO("Not yet implemented")
    }

    private fun navigateToBack() {
        emitEvent(MainEvent.NavigateBack)
    }


    private fun navigateToAddTransactions() {
        emitEvent(MainEvent.NavigateToAddTransAction)
    }

    fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.update {
                it.copy(isSyncEnabled = currentUser != null)
            }
        }
    }

    private fun onTypeSelected(type: TypeOfOperation) {
        _uiState.update { it.copy(selectedType = type) }
        loadTransactions()
    }

    private fun onMonthSelected(month: Months) {
        _uiState.update { it.copy(selectedMonth = month) }
        loadTransactions()
    }

    private fun loadTransactions() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Имитация загрузки или вызов репозитория
                // val data = repository.get(uiState.value.selectedMonth, uiState.value.selectedType)
                // _uiState.update { it.copy(transactions = data, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                emitEvent(MainEvent.ShowSnackbar("Ошибка загрузки"))
            }
        }
    }

    private fun emitEvent(event: MainEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}