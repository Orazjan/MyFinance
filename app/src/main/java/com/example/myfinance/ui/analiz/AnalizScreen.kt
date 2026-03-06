package com.example.myfinance.ui.analiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Column(modifier = Modifier.padding(innerPadding)) {}
    }
}