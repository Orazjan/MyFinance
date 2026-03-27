package com.example.myfinance.ui.main

import com.example.myfinance.domain.model.Months
import com.example.myfinance.domain.model.TypeOfOperation

sealed interface MainAction {
    data object OnAddTransactionClick : MainAction
    data object OnBackClick : MainAction
    data object OnDeleteClick : MainAction
    data class OnTransactionClick(val transactionId: Long) : MainAction
    data class OnDeleteTransactionClick(val transactionId: Long) : MainAction
    data object OnShowDetailClick : MainAction
    data class OnTypeSelected(val type: TypeOfOperation) : MainAction
    data class OnMonthSelected(val month: Months) : MainAction
}