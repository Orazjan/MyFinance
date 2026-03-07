package com.example.myfinance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfinance.ui.theme.MyFinanceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimarySpinner(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null

) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = {
            expanded = it
        }, modifier = modifier

    ) {
        PrimaryOutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            errorMessage = errorMessage,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
                    onOptionSelected(selectionOption)
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimarySpinnerPreview() {
    MyFinanceTheme {
        val categories = listOf("Продукты", "Транспорт", "Развлечения", "Здоровье")
        var selected by remember { mutableStateOf(categories[0]) }

        PrimarySpinner(
            options = categories,
            selectedOption = selected,
            onOptionSelected = { selected = it },
            label = "Категория расходов"
        )
    }
}