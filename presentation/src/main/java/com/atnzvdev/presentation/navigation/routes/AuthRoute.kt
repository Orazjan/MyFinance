package com.atnzvdev.presentation.navigation.routes

import AuthDestination
import Graph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.atnzvdev.presentation.ui.auth.login.AuthScreen
import com.atnzvdev.presentation.ui.auth.registration.RegistrationScreen
import com.atnzvdev.presentation.ui.auth.resetPassword.ResetPasswordScreen

fun NavGraphBuilder.AuthRoute(navController: NavHostController) {
    navigation<Graph.Auth>(
        startDestination = AuthDestination.Login
    ) {
        composable<AuthDestination.Login> {
            AuthScreen(navController = navController)
        }

        composable<AuthDestination.ResetPassword> {
            ResetPasswordScreen(navController = navController)
        }

        composable<AuthDestination.Registration> {
            RegistrationScreen(navController = navController)
        }
    }
}