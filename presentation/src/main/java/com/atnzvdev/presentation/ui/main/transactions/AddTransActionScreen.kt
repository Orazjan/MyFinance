package com.atnzvdev.presentation.ui.main.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atnzvdev.domain.model.Template
import com.atnzvdev.domain.model.TypeOfOperation
import com.atnzvdev.presentation.ui.components.PrimaryOutlinedTextField
import com.atnzvdev.presentation.ui.components.PrimarySpinner
import com.atnzvdev.presentation.ui.components.PrimaryText
import com.atnzvdev.presentation.ui.components.TopNavBar

@Composable
fun AddTransActionScreen(
    onBackClick: () -> Unit, viewModel: AddTransActionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.action.collect { action ->
            when (action) {
                is TransactionAction.NavigateBack -> onBackClick()
                is TransactionAction.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = action.message)
                }
            }
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
    }, topBar = {
        TopNavBar(
            title = "Новая Транзакция",
            onBackClick = { onBackClick() },
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        )
    }) { innerpadding ->
        Column(
            modifier = Modifier
                .padding(innerpadding)
                .padding(16.dp)
        ) {

            TemplateSelector(
                templates = state.templates,
                selectedIndex = state.selectedIndex,
                onSelected = {
                    viewModel.onAction(TransactionEvent.OnTemplateSelected(it))
                }
            )

            Spacer(Modifier.height(16.dp))

            TransactionForm(
                state = state, onAction = viewModel::onAction
            )
        }
    }


}

@Composable
fun TransactionForm(
    state: AddTransActionUiState, onAction: (TransactionEvent) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        PrimaryOutlinedTextField(
            value = state.nameInput,
            onValueChange = {
                onAction(TransactionEvent.OnNameChanged(it))
            }, isError = state.nameError != null, errorMessage = state.nameError ?: "",
            label = { Text("Название") }, keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryOutlinedTextField(
            value = state.amountInput,
            onValueChange = {
                onAction(TransactionEvent.OnAmountChanged(it))
            },
            isError = state.amountError != null, errorMessage = state.amountError ?: "",
            label = { Text("Сумма") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        PrimaryOutlinedTextField(
            value = state.description,
            onValueChange = {
                onAction(TransactionEvent.OnDescriptionChanged(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxWidth()
        )

        PrimarySpinner(
            options = TypeOfOperation.entries.map { it.nameOfType },
            selectedOption = state.typeOfOperation.name,
            onOptionSelected = { selectedName ->
                TypeOfOperation.fromDisplayName(selectedName)?.let { type ->
                    onAction(TransactionEvent.OnTypeChanged(type))
                }
            }, isError = state.typeError != null, errorMessage = state.typeError ?: "",
            label = "Общее",
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(15.dp))

        if (state.generalError != null) {
            PrimaryText(
                text = state.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            enabled = !state.isLoading && state.typeError == null && state.amountError == null && state.nameError == null,
            onClick = {
                onAction(TransactionEvent.OnSaveClicked)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }
    }
}

@Composable
fun TemplateSelector(
    templates: List<Template>,
    selectedIndex: Int,
    onSelected: (Template) -> Unit
) {

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        itemsIndexed(templates) { index, template ->

            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelected(template) },
                label = { Text(template.name) }
            )
        }
    }
}
