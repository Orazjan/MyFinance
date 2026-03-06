package com.example.myfinance.navigation.routes

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.profile.ProfileScreen

fun NavGraphBuilder.ProfileRoute(navController: NavHostController) {
    navigation(startDestination = "profile_screen", route = Graph.Profile) {
        composable("profile_screen") {
            ProfileScreen(

            )
        }
    }
}