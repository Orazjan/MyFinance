package com.example.myfinance.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Payments
import androidx.compose.material.icons.twotone.TrendingDown
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfinance.domain.model.Months
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryLazyColumn
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.TopNavBar
import com.example.myfinance.ui.components.rememberScrollingDirection

@Composable
fun MainScreen(scrollState: LazyListState) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "My Finance",
                onBackClick = null,
                modifier = Modifier
                    .padding(0.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.TwoTone.Add, contentDescription = "Добавить")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                PrimaryText(
                    "Общий баланс",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {

                    PrimaryText(
                        "1500000",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PrimaryText(
                        "$",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            PrimaryCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                elevation = (2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFBA1A1A)
                            )
                            Spacer(Modifier.width(8.dp))
                            PrimaryText(
                                text = "Всего расходов",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {

                            Text(
                                text = "150 000 000",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "$",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        }
                    }
                    Icon(
                        imageVector = Icons.TwoTone.Payments,
                        contentDescription = "",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Column {
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
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                            Column {
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
}