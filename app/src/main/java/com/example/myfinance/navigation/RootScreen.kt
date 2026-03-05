package com.example.myfinance.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.BottomBar

@Composable
fun RootScreen() {

    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(padding))
    }
}