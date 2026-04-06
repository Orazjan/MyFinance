package com.atnzvdev.presentation.ui.profile.templates.addTemplate

import com.atnzvdev.domain.model.TypeOfOperation

sealed interface AddTemplateAction {
    data class OnNameChanged(val name: String) : AddTemplateAction
    data class OnAmountChanged(val amount: String) : AddTemplateAction
    data class OnTypeSelected(val type: TypeOfOperation) : AddTemplateAction
    data object OnBackClick : AddTemplateAction
    data object OnSaveClick : AddTemplateAction
}