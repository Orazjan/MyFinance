package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun RootScreen() {

    val navController = rememberNavController()

    AppNavHost(
        navController = navController,
        modifier = Modifier
    )
}