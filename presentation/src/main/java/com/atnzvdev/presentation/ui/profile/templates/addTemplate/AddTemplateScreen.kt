package com.atnzvdev.presentation.ui.profile.templates.addTemplate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Money
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.atnzvdev.domain.model.TypeOfOperation
import com.atnzvdev.presentation.ui.components.PrimaryButton
import com.atnzvdev.presentation.ui.components.PrimaryCard
import com.atnzvdev.presentation.ui.components.PrimaryOutlinedTextField
import com.atnzvdev.presentation.ui.components.PrimarySpinner
import com.atnzvdev.presentation.ui.components.PrimaryText

@Composable
fun AddTemplateScreen(onBackClick: () -> Unit, viewModel: AddTemplateViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Spacer(Modifier.height(10.dp))
    PrimaryText("Создать шаблон", style = MaterialTheme.typography.labelLarge)
    PrimaryCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            PrimaryOutlinedTextField(
                value = state.nameInput,
                onValueChange = { viewModel.onAction(AddTemplateAction.OnNameChanged(it)) },
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
                                viewModel.onAction(
                                    AddTemplateAction.OnNameChanged(
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

            PrimaryOutlinedTextField(
                value = state.amountInput,
                onValueChange = { viewModel.onAction(AddTemplateAction.OnAmountChanged(it)) },
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
                            onClick = { viewModel.onAction(AddTemplateAction.OnAmountChanged("")) })
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
                onClick = { viewModel.onAction(AddTemplateAction.OnSaveClick) },
                enabled = true
            )
        }
    }
}