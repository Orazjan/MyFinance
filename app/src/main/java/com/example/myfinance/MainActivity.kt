/**
 *
 * Atanyazov Oraz 2024
 *
 */
package com.example.myfinance;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.myfinance.navigation.AppNavHost
import com.example.myfinance.ui.theme.MyFinanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinanceTheme {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}