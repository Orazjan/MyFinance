package com.example.myfinance.navigation.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.auth.AuthScreen

fun NavGraphBuilder.AuthRoute(navController: NavHostController) {
    navigation(startDestination = "auth_screen", route = Graph.Auth) {
        composable("auth_screen") {
            AuthScreen(

            )
        }
    }

}