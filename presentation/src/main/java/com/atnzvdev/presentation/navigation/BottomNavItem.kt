package com.atnzvdev.presentation.navigation

import MainDestinations
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BarChart
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: Any, val title: String, val icon: ImageVector
) {
    data object Analytics : BottomNavItem(
        route = MainDestinations.Analytics, title = "Аналитика", icon = Icons.TwoTone.BarChart
    )

    data object Home : BottomNavItem(
        route = MainDestinations.Home, title = "Главная", icon = Icons.TwoTone.Home
    )

    data object Profile : BottomNavItem(
        route = MainDestinations.Profile, title = "Профиль", icon = Icons.TwoTone.Person
    )
}