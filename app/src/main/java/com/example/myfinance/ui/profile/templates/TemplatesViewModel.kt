package com.example.myfinance.ui.profile.templates

import androidx.lifecycle.ViewModel
import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(TemplateUiState())
    var uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()


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
                onSave()
            }


        }
    }

    private fun onSave() {
        val currentState = _uiState.value

        val newItem = Templates(
            id = System.currentTimeMillis().toInt(),
            name = currentState.nameInput,
            amount = currentState.amountInput,
            isIncome = currentState.selectedType == TypeOfOperation.INCOME
        )
        _uiState.update {
            it.copy(
                items = it.items + newItem,
                nameInput = "",
                amountInput = ""
            )
        }
    }

    private fun onLoad() {
        TODO("Not yet implemented")
    }


}