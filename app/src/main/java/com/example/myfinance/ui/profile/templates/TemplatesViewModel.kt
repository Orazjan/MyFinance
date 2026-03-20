package com.example.myfinance.ui.profile.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation
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
class TemplatesViewModel @Inject constructor(

) : ViewModel() {
    private val _navigation = MutableSharedFlow<Unit>()
    val navigation = _navigation.asSharedFlow()

    private val _uiState = MutableStateFlow(TemplateUiState())
    val uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()


    fun onEvent(event: TemplateEvent) {
        when (event) {
            is TemplateEvent.OnNameChanged -> {
                _uiState.update {
                    it.copy(
                        nameInput = event.name
                    )
                }
            }

            is TemplateEvent.OnAmountChanged -> {
                _uiState.update {
                    it.copy(
                        amountInput = event.amount
                    )
                }
            }

            is TemplateEvent.OnTypeChanged -> {
                _uiState.update {
                    it.copy(
                        selectedType = event.type
                    )
                }
            }

            is TemplateEvent.OnLoad -> {
                onLoad()
            }

            is TemplateEvent.OnSaveClick -> {
                onSave(event.onSuccess)
            }


        }
    }

    private fun onSave(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        val newItem = Templates(
            id = System.currentTimeMillis().toInt(),
            name = currentState.nameInput,
            amount = currentState.amountInput.toDoubleOrNull() ?: 0.0,
            isIncome = currentState.selectedType == TypeOfOperation.INCOME
        )
        _uiState.update {
            it.copy(
                items = it.items + newItem,
                nameInput = "",
                amountInput = ""
            )
        }
        viewModelScope.launch {
            _navigation.emit(Unit)
        }
    }

    private fun onLoad() {
        TODO("Not yet implemented")
    }


}