package com.example.myfinance.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.analiz.AnalizScreen
import com.example.myfinance.ui.main.MainScreen
import com.example.myfinance.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun MainFlowScreen() {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        NavigationBar {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.primary,    //Текст
                indicatorColor = MaterialTheme.colorScheme.primary,         //Обводка
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,    //Когда выделено
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            NavigationBarItem(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                icon = {
                    Icon(
                        Icons.Default.Analytics, contentDescription = "Analiz Icon"
                    )
                },
                label = { Text("Аналитика") },
                colors = navItemColors
            )

            NavigationBarItem(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home Icon") },
                label = { Text("Главная") },
                colors = navItemColors
            )

            NavigationBarItem(
                selected = pagerState.currentPage == 2,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile Icon") },
                label = { Text("Профиль") },
                colors = navItemColors
            )
        }
    }) { padding ->
        HorizontalPager(
            state = pagerState, modifier = Modifier
                .padding(
                    bottom = padding.calculateBottomPadding()
                )
                .fillMaxSize(),

            ) { page ->
            when (page) {
                0 -> AnalizScreen(
                    onGoToMain = { coroutineScope.launch { pagerState.animateScrollToPage(1) } })
                1 -> MainScreen()
                2 -> ProfileScreen(
                    onGoToMain = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    })
            }
        }
    }
}
