package com.atnzvdev.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atnzvdev.presentation.ui.theme.MyFinanceTheme

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
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary.copy(0.5f)),
            shadowElevation = 5.dp,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
                    onOptionSelected(selectionOption)
                    expanded = false
                })
                HorizontalDivider(
                    Modifier.height(3.dp),
                    color = MaterialTheme.colorScheme.primary.copy(0.3f)
                )
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