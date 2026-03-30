package com.example.myfinance.ui.main.transactions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.model.Transaction
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransActionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val validate: TransactionValidate
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransActionUiState())
    val uiState: StateFlow<AddTransActionUiState> = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<TransactionAction>()
    val action = _action.asSharedFlow()

    fun onAction(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnNameChanged -> {
                when (val result = validate.validateName(event.newName)) {
                    is ValidationResult.Error -> {
                        _uiState.update {
                            it.copy(
                                nameInput = event.newName, nameError = result.message
                            )
                        }
                    }

                    is ValidationResult.Success -> {
                        _uiState.update {
                            it.copy(
                                nameInput = event.newName, nameError = null
                            )
                        }
                    }
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
                            TypeOfOperation.INCOME
                        else
                            TypeOfOperation.EXPENSES
                    )
                }
            }

            is TransactionEvent.OnAmountChanged -> {
                when (val result = validate.validateAmount(event.newAmount)) {
                    is ValidationResult.Success -> {
                        _uiState.update {
                            it.copy(
                                amountInput = event.newAmount, amountError = null
                            )
                        }
                    }

                    is ValidationResult.Error -> {
                        _uiState.update {
                            it.copy(
                                amountInput = event.newAmount, amountError = result.message
                            )
                        }
                    }
                }

            }

            is TransactionEvent.OnTypeChanged -> {
                when (val result = validate.validateType(event.newType)) {
                    is ValidationResult.Error -> {
                        _uiState.update {
                            it.copy(
                                typeOfOperation = event.newType, typeError = result.message
                            )
                        }
                    }

                    is ValidationResult.Success -> {
                        _uiState.update {
                            it.copy(
                                typeOfOperation = event.newType, typeError = null
                            )
                        }
                    }
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
        _uiState.update { it.copy(isLoading = true) }

        val amount = state.amountInput.toDoubleOrNull()
            ?: return

        val transaction = Transaction(
            title = state.nameInput,
            description = state.description, type = state.typeOfOperation,
            amount = amount,
            date = System.currentTimeMillis()
        )
        when (val result = validate.validateTransaction(transaction)) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        generalError = result.message, isLoading = false
                    )
                }
            }

            is ValidationResult.Success -> {
                addTransaction(transaction)
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                _action.emit(TransactionAction.ShowSnackbar("Успешно добавлено"))
                delay(500)
                _action.emit(TransactionAction.NavigateBack)
            } catch (e: Exception) {
                Log.d("TransactionError", e.message.toString())
                _action.emit(TransactionAction.ShowSnackbar("Ошибка в добавлении"))
            }

        }

    }

    private fun onDownloadCategories() {
        TODO("Not yet implemented")
    }
}