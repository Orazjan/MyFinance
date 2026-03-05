package com.example.myfinance.navigation.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.analiz.AnalizScreen

fun NavGraphBuilder.AnalizRoute(navController: NavHostController) {
    navigation(startDestination = "analiz_screen", route = Graph.Analiz) {
        composable("analiz_screen") {
            AnalizScreen(

            )
        }
    }
}