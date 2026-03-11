package com.example.myfinance.navigation.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.auth.AuthScreen
import com.example.myfinance.ui.auth.RegistrationScreen
import com.example.myfinance.ui.auth.ResetPasswordScreen

fun NavGraphBuilder.AuthRoute(navController: NavHostController) {
    navigation(startDestination = Graph.AuthScreenRoot, route = Graph.Auth) {
        composable(route = Graph.AuthScreenRoot) {
            AuthScreen(
                navController = navController
            )
        }
        composable(route = Graph.ResetPassword) {
            ResetPasswordScreen {
                navController.popBackStack()
            }
        }
        composable(route = Graph.Registration) {
            RegistrationScreen {
                navController.popBackStack()
            }
        }
    }
}