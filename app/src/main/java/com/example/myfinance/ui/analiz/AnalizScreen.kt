package com.example.myfinance.ui.analiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfinance.ui.Months
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun AnalizScreen(
    onGoToMain: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Аналитика",
                onBackClick = { onGoToMain() },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val months = Months.entries.map { it.displayName }
                var selectedMonth: Months by remember { mutableStateOf(Months.ALL_PERIOD) }
                val variants = listOf("Общее", "Доходы", "Расходы")
                var variant by remember { mutableStateOf(variants[0]) }

                PrimarySpinner(
                    options = variants,
                    selectedOption = variant,
                    onOptionSelected = { variant = it },
                    label = "Общее",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f)
                )

                PrimarySpinner(
                    options = months,
                    selectedOption = selectedMonth.displayName,
                    onOptionSelected = { navSelection ->
                        selectedMonth = Months.entries.first { it.displayName == navSelection }
                    },
                    label = "Месяцы",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f)
                )
            }
        }
    }
}