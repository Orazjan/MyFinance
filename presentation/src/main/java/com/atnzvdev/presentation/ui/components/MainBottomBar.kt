package com.atnzvdev.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.atnzvdev.presentation.navigation.BottomNavItem

@Composable
fun MainBottomBar(
    items: List<BottomNavItem>,
    currentDestination: NavDestination?,
    onItemClick: (BottomNavItem) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(64.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            val navItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            items.forEach { item ->
                val isSelected = currentDestination?.hasRoute(item.route::class) == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemClick(item) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = navItemColors
                )
            }
        }
    }
}