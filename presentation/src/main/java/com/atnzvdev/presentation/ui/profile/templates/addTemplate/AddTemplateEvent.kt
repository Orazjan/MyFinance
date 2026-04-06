package com.atnzvdev.presentation.ui.profile.templates.addTemplate

import com.atnzvdev.domain.model.TypeOfOperation

sealed interface AddTemplateEvent {
    data class OnNameChanged(val name: String) : AddTemplateEvent
    data class OnAmountChanged(val amount: String) : AddTemplateEvent
    data class OnTypeChanged(val type: TypeOfOperation) : AddTemplateEvent
    data class ShowSnackbar(val message: String) : AddTemplateEvent

}