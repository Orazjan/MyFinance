package com.atnzvdev.presentation.ui.main

import com.atnzvdev.domain.model.Months
import com.atnzvdev.domain.model.TypeOfOperation

sealed interface MainAction {
    data object OnAddTransactionClick : MainAction
    data object OnDeleteClick : MainAction
    data object OnUpdateBalanceClick : MainAction
    data class OnTransactionClick(val transactionId: Long) : MainAction
    data class OnDeleteTransactionClick(val transactionId: Long) : MainAction
    data object OnShowDialogAlertClick : MainAction
    data object OnDismissTransactionDetails : MainAction
    data class OnTypeSelected(val type: TypeOfOperation) : MainAction
    data class OnMonthSelected(val month: Months) : MainAction
}