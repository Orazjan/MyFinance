package com.example.myfinance.navigation.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.main.MainScreen

fun NavGraphBuilder.MainRoute(navController: NavHostController) {
    navigation(startDestination = "main_screen", route = Graph.Main) {
        composable("main_screen") {
            MainScreen(

            )
        }
    }
}