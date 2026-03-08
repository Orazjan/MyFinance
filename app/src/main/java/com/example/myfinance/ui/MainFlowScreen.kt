package com.example.myfinance.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.myfinance.ui.analiz.AnalizScreen
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.main.MainScreen
import com.example.myfinance.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun MainFlowScreen() {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .size(100.dp)
                .padding(
                    vertical = 16.dp, horizontal = 50.dp
                ), contentAlignment = Alignment.Center
        ) {
            NavigationBar(
                tonalElevation = 0.dp,
                modifier = Modifier
                    .height(60.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            ) {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.primary,    //Текст
                indicatorColor = MaterialTheme.colorScheme.primary,         //Обводка
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,    //Когда выделено
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            NavigationBarItem(
                modifier = Modifier.size(24.dp),

                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                icon = {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = "Analiz Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }, label = {
                    PrimaryText(
                        "Аналитика", style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = navItemColors
            )

            NavigationBarItem(
                modifier = Modifier.size(24.dp),
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }, icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }, label = {
                    PrimaryText(
                        "Главная", style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = navItemColors
            )

            NavigationBarItem(
                modifier = Modifier.size(24.dp),
                selected = pagerState.currentPage == 2,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }, icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }, label = {
                    PrimaryText(
                        "Профиль", style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = navItemColors
            )
        }
        }
    }) { padding ->
        HorizontalPager(
            state = pagerState
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
