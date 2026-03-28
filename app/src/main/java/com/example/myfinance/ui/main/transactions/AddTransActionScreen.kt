package com.example.myfinance.ui.main.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myfinance.domain.model.Templates
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun AddTransActionScreen(
    onBackClick: () -> Unit, viewModel: AddTransActionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.templates.size })
    val scope = rememberCoroutineScope()


    Scaffold(topBar = {
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

        OutlinedTextField(
            value = state.nameInput,
            onValueChange = {
                onAction(TransactionEvent.OnNameChanged(it))
            },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.amountInput,
            onValueChange = {
                onAction(TransactionEvent.OnAmountChanged(it))
            },
            label = { Text("Сумма") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = {
                onAction(TransactionEvent.OnDescriptionChanged(it))
            },
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxWidth()
        )

        PrimarySpinner(
            options = TypeOfOperation.entries.map { it.nameOfType },
            selectedOption = state.typeOfOperation.nameOfType,
            onOptionSelected = { selectedName ->
                TypeOfOperation.fromString(selectedName)?.let { type ->
                    onAction(TransactionEvent.OnTypeChanged(type))
                }
            },
            label = "Общее",
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                onAction(TransactionEvent.OnSaveClicked)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}

@Composable
fun TemplateSelector(
    templates: List<Templates>,
    selectedIndex: Int,
    onSelected: (Templates) -> Unit
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
