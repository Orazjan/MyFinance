package com.example.myfinance.ui.profile.templates

import androidx.lifecycle.ViewModel
import com.example.myfinance.domain.model.Templates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(TemplatesUiState.Loading)
    var uiState: StateFlow<TemplatesUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    fun onEvents(event: TemplatesEvent) {
        when (event) {
            is TemplatesEvent.Add -> addTemplate(event.template)
            is TemplatesEvent.Delete -> deleteTemplate(event.id)
            is TemplatesEvent.Edit -> editTemplate(event.template)
            is TemplatesEvent.Refresh -> loadTemplates()
        }
    }

    private fun editTemplate(templates: Templates) {
        TODO("Not yet implemented")
    }

    private fun deleteTemplate(id: Int) {
        TODO("Not yet implemented")
    }

    private fun addTemplate(templates: Templates) {
        TODO("Not yet implemented")
    }

    private fun loadTemplates() {
        TODO("Not yet implemented")
    }

}