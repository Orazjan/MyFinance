package com.example.myfinance.ui.profile.templates

import com.example.myfinance.domain.model.Templates

sealed interface TemplatesEvent {
    data class Add(val template: Templates) : TemplatesEvent
    data class Delete(val id: Int) : TemplatesEvent
    data class Edit(val template: Templates) : TemplatesEvent
    object Refresh : TemplatesEvent
}
