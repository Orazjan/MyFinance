package com.example.myfinance.ui.profile.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Money
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryLazyColumn
import com.example.myfinance.ui.components.PrimaryOutlinedTextField
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun PatternScreen(onBackClick: () -> Unit, viewModel: TemplatesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Шаблоны",
                onBackClick = { onBackClick() },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        }) { innerpadding ->
        Column(
            modifier = Modifier
                .padding(innerpadding)
                .padding(16.dp)

        ) {

            PrimaryText("Сохранённые шаблоны", style = MaterialTheme.typography.labelLarge)
            PrimaryLazyColumn(
                Modifier.padding(10.dp)

            ) {
                items(2) { entry ->
                    PrimaryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.Fastfood, contentDescription = ""
                            )
                            Row(
                                modifier = Modifier
                                    .weight(2f)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PrimaryText(entry.toString())
                                Column {
                                    PrimaryText("Другое")
                                    PrimaryText("Расход")

                                }
                                PrimaryText("200")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            PrimaryText("Создать шаблон", style = MaterialTheme.typography.labelLarge)
            PrimaryCard(
                modifier = Modifier.padding(15.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    PrimaryOutlinedTextField(
                        value = state.nameInput,
                        onValueChange = { viewModel.onEvent(TemplateEvent.OnNameChanged(it)) },
                        isError = false,
                        errorMessage = "Не подходит",
                        label = {
                            PrimaryText(
                                "Название шаблона",
                                color = MaterialTheme.colorScheme.primary.copy(0.5f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.TwoTone.Edit, contentDescription = "Name"
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.TwoTone.Clear,
                                contentDescription = "Name",
                                modifier = Modifier.clickable(
                                    true, onClick = {
                                        viewModel.onEvent(
                                            TemplateEvent.OnNameChanged(
                                                ""
                                            )
                                        )
                                    })
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            capitalization = KeyboardCapitalization.Words,
                            showKeyboardOnFocus = true,
                            imeAction = ImeAction.Next
                        ),
                        shape = OutlinedTextFieldDefaults.shape
                    )
                    Spacer(Modifier.height(10.dp))
                    //
                    PrimaryOutlinedTextField(
                        value = state.amountInput,
                        onValueChange = { viewModel.onEvent(TemplateEvent.OnAmountChanged(it)) },
                        isError = false,
                        errorMessage = "Неn суммы",
                        label = {
                            PrimaryText(
                                "Сумма", color = MaterialTheme.colorScheme.primary.copy(0.5f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.TwoTone.Money, contentDescription = "Name"
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.TwoTone.Clear,
                                contentDescription = "Name",
                                modifier = Modifier.clickable(
                                    true,
                                    onClick = { viewModel.onEvent(TemplateEvent.OnAmountChanged("")) })
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number,
                            showKeyboardOnFocus = true,
                            imeAction = ImeAction.Next
                        ),
                        shape = OutlinedTextFieldDefaults.shape
                    )
                    Spacer(Modifier.height(10.dp))
                    //
                    val types = TypeOfOperation.entries
                    var selectedType by remember { mutableStateOf(types[0]) }
                    PrimarySpinner(
                        options = types.map { it.nameOfType },
                        selectedOption = selectedType.nameOfType,
                        onOptionSelected = { selectedName ->
                            selectedType = types.find { it.nameOfType == selectedName } ?: types[0]
                        },
                        label = "Выберите операцию"
                    )
                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        "Сохранить изменения",
                        onClick = { viewModel.onEvent(TemplateEvent.OnSaveClick { onBackClick() }) },
                        enabled = true
                    )
                }
            }
        }
    }
}