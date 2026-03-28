package com.example.myfinance.ui.main.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransActionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransActionUiState())
    val uiState: StateFlow<AddTransActionUiState> = _uiState.asStateFlow()

    fun onAction(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnNameChanged -> {
                _uiState.update {
                    it.copy(
                        nameInput = event.newName
                    )
                }
            }

            is TransactionEvent.OnTemplateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedTemplate = event.template,
                        selectedIndex = it.templates.indexOf(event.template),

                        nameInput = event.template.name,
                        amountInput = event.template.amount.toString(),
                        typeOfOperation = if (event.template.isIncome)
                            TypeOfOperation.ALL
                        else
                            TypeOfOperation.EXPENSES
                    )
                }

            }

            is TransactionEvent.OnAmountChanged -> {
                _uiState.update {
                    it.copy(
                        amountInput = event.newAmount
                    )
                }
            }

            is TransactionEvent.OnTypeChanged -> {
                _uiState.update {
                    it.copy(
                        typeOfOperation = event.newType
                    )
                }
            }

            is TransactionEvent.OnCategorySelected -> {
                _uiState.update {
                    it.copy(
                        selectedIndex = event.index
                    )
                }
            }

            is TransactionEvent.OnDescriptionChanged -> {
                _uiState.update {
                    it.copy(
                        description = event.newDescription
                    )
                }
            }

            is TransactionEvent.DowloadCategories -> {
                onDownloadCategories()
            }

            is TransactionEvent.OnSaveClicked -> {
                onSaveClick()
            }
        }
    }


    private fun onSaveClick() {
        val state = _uiState.value

        val amount = state.amountInput.toDoubleOrNull()
            ?: return

        val transaction = TransactionEntity(
            title = state.nameInput,
            description = state.description,
            type = state.typeOfOperation.name,
            amount = amount,
            date = System.currentTimeMillis()
        )
        addTransaction(transaction)
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }

    private fun onDownloadCategories() {
        TODO("Not yet implemented")
    }
}