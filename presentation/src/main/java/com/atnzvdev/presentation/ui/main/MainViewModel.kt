package com.atnzvdev.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atnzvdev.domain.model.Months
import com.atnzvdev.domain.model.TypeOfOperation
import com.atnzvdev.domain.repository.AuthRepository
import com.atnzvdev.domain.repository.TransactionRepository
import com.atnzvdev.domain.useCase.GetFinanceSummaryUseCase
import com.atnzvdev.domain.useCase.UpdateCurrentBalanceUseCase
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
    private val repository: TransactionRepository,
    private val authRepository: AuthRepository,
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val updateCurrentBalanceUseCase: UpdateCurrentBalanceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events = _events.asSharedFlow()

    private var transactionsJob: Job? = null

    init {
        checkAuthState()
        observeTransactions()
        observeFinanceSummary()
    }

    fun onAction(actions: MainAction) {
        when (actions) {
            is MainAction.OnAddTransactionClick -> navigateToAddTransactions()
            is MainAction.OnUpdateBalanceClick -> {}
            is MainAction.OnDeleteClick -> onDeleteClick()
            is MainAction.OnDeleteTransactionClick -> TODO()
            is MainAction.OnShowDialogAlertClick -> onDetailClick()
            is MainAction.OnTransactionClick -> TODO()
            is MainAction.OnMonthSelected -> onMonthSelected(actions.month)
            is MainAction.OnTypeSelected -> onTypeSelected(actions.type)
            is MainAction.OnDismissTransactionDetails -> onDismissDetail()
        }
    }

    private fun onDismissDetail(): Nothing {
        TODO()
    }

    private fun observeFinanceSummary() {
        viewModelScope.launch {
            getFinanceSummaryUseCase().collect { value ->
                _uiState.update {
                    it.copy(
                        totalExpense = value.totalExpense.toString(),
                        totalBalance = value.currentBalance.toString()
                    )
                }
            }
        }
    }

    private fun onDeleteClick() {

    }

    private fun onDetailClick() {
        viewModelScope.launch {
            _events.emit(MainEvent.ShowSnackbar("Открытие Деталей"))
        }
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
        transactionsJob?.cancel()
        transactionsJob = viewModelScope.launch {
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