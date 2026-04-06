package com.atnzvdev.presentation.ui.profile.versionOfApp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atnzvdev.presentation.ui.components.TopNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VersionInfoScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Информация о версии",
                onBackClick = { onBackClick() },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        }) {

    }
}