package com.example.myfinance.ui.main.transActions

import androidx.lifecycle.ViewModel
import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.Transaction
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddTransActionViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransActionUiState())
    val uiState: StateFlow<AddTransActionUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                templates = listOf(
                    Templates(1, "Еда", false, 200.0),
                    Templates(2, "Зарплата", true, 50000.0),
                    Templates(3, "Такси", false, 300.0)
                )
            )
        }
    }

    fun onEvent(event: TransActionEvent) {
        when (event) {
            is TransActionEvent.OnNameChanged -> {
                _uiState.update {
                    it.copy(
                        nameInput = event.newName
                    )
                }
            }

            is TransActionEvent.OnTemplateSelected -> {
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

            is TransActionEvent.OnAmountChanged -> {
                _uiState.update {
                    it.copy(
                        amountInput = event.newAmount
                    )
                }
            }

            is TransActionEvent.OnTypeChanged -> {
                _uiState.update {
                    it.copy(
                        typeOfOperation = event.newType
                    )
                }
            }

            is TransActionEvent.OnCategorySelected -> {
                _uiState.update {
                    it.copy(
                        selectedIndex = event.index
                    )
                }
            }

            is TransActionEvent.OnDescriptionChanged -> {
                _uiState.update {
                    it.copy(
                        description = event.newDescription
                    )
                }
            }

            is TransActionEvent.DowloadCategories -> {
                onDownloadCategories()
            }

            is TransActionEvent.OnSaveClicked -> {
                onSaveClick(event.onSuccess)
            }
        }
    }


    private fun onSaveClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        val amount = state.amountInput.toDoubleOrNull()
            ?: return

        val transaction = Transaction(
            name = state.nameInput,
            description = state.description,
            type = state.typeOfOperation,
            amount = amount,
            timeStamp = System.currentTimeMillis()
        )
        onSuccess()

    }

    private fun onDownloadCategories() {
        TODO("Not yet implemented")
    }


}