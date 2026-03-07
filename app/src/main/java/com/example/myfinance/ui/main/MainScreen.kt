package com.example.myfinance.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myfinance.ui.Months
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryLazyColumn
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "My Finance",
                onBackClick = null,
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            PrimaryCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(5.dp, MaterialTheme.colorScheme.primary),

                ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrimaryText(
                        text = "Остаток $:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Spacer(Modifier.weight(1f))

                    PrimaryText(
                        text = "150 000 000 ₽",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,

                        )
                }
            }
            PrimaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                border = BorderStroke(5.dp, MaterialTheme.colorScheme.primary),

                ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrimaryText(
                        text = "Расходы:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Spacer(Modifier.weight(1f))

                    PrimaryText(
                        text = "150 000 000 ₽",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,

                        )
                }
            }
            val months = Months.entries.map { it.displayName }
            var selectedMonth: Months by remember { mutableStateOf(Months.ALL_PERIOD) }
            PrimarySpinner(
                options = months,
                selectedOption = selectedMonth.displayName,
                onOptionSelected = { navSelection ->
                    selectedMonth = Months.entries.first { it.displayName == navSelection }
                },
                label = "Месяцы",
                modifier = Modifier.padding(top = 10.dp)
            )

            PrimaryLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "История операций",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(10) { index ->
                    PrimaryCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Image, contentDescription = "", modifier = Modifier
                                    .clip(
                                        CircleShape
                                    )
                                    .size(30.dp)
                            )
                            Column() {
                                Text(text = "Покупка продуктов ${index + 1}")
                                Text(text = "Расход")
                            }


                            Text(
                                text = "- 1 500 ₽", color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
