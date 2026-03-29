package com.example.myfinance.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.useCase.GetFinanceSummaryUseCase
import com.example.myfinance.domain.useCase.UpdateCurrentBalanceUseCase
import com.example.myfinance.domain.model.Months
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.AuthRepository
import com.example.myfinance.domain.repository.TransactionRepository
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
class MainViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val authRepository: AuthRepository,
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val updateCurrentBalanceUseCase: UpdateCurrentBalanceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events = _events.asSharedFlow()

    init {
        checkAuthState()
        observeTransactions()
        observeFinanceSummary()
    }

    private fun observeFinanceSummary() {
        viewModelScope.launch {
            getFinanceSummaryUseCase().collect { value ->
                _uiState.update {
                    it.copy(
                        totalExpense = value.totalExpense.toString(),
                        totalBalance = value.baseBalance.toString()
                    )
                }
            }
        }
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
        observeTransactions()
    }

    private fun onMonthSelected(month: Months) {
        _uiState.update { it.copy(selectedMonth = month) }
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions().collect { transactions ->
                _uiState.update {
                    it.copy(transactions = transactions)
                }
            }
        }

    }

    private fun emitEvent(event: MainEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}