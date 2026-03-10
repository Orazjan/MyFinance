package com.example.myfinance.ui.profile

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun VersionInfoScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Информация о версии",
                onBackClick = { onBackClick() },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        }) {}
}