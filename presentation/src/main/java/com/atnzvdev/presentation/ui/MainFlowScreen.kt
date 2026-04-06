package com.atnzvdev.presentation.ui

import AppDestination
import Graph
import MainDestinations
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.atnzvdev.presentation.navigation.BottomNavItem
import com.atnzvdev.presentation.ui.analiz.AnalizScreen
import com.atnzvdev.presentation.ui.components.MainBottomBar
import com.atnzvdev.presentation.ui.main.MainScreen
import com.atnzvdev.presentation.ui.profile.profile.ProfileScreen

@Composable
fun MainFlowScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    val items = listOf(
        BottomNavItem.Analytics, BottomNavItem.Home, BottomNavItem.Profile
    )

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            MainBottomBar(
                items = items,
                currentDestination = currentDestination,
                onItemClick = { item ->
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                // УДАЛИ currentRoute отсюда совсем
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = MainDestinations.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<MainDestinations.Analytics> {
                AnalizScreen(onGoToMain = {
                    bottomNavController.navigate(
                        Graph.Main
                    )
                })
            }
            composable<MainDestinations.Home> {
                MainScreen(onNavigateToAddTransaction = {
                    bottomNavController.navigate(
                        AppDestination.AddTransaction
                    )
                })
            }
            composable<MainDestinations.Profile> {
                ProfileScreen(
                    onGoToMain = {
                        bottomNavController.navigate(
                            Graph.Main
                        )
                    }, onGoToAuth = { bottomNavController.navigate(Graph.Auth) },

                    onGoToPattern = { bottomNavController.navigate(AppDestination.Templates) },
                    onGoToSettings = { bottomNavController.navigate(AppDestination.Settings) }
                )
            }
        }
    }
}