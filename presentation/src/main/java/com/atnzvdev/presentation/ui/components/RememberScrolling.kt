package com.atnzvdev.presentation.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberScrollingDirection(state: LazyListState): Boolean {
    var isVisible by remember { mutableStateOf(true) }

    var previousScrollOffset by remember { mutableIntStateOf(0) }
    var previousIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.firstVisibleItemScrollOffset, state.firstVisibleItemIndex) {
        val currentIndex = state.firstVisibleItemIndex
        val currentOffset = state.firstVisibleItemScrollOffset

        val isScrollingDown = if (currentIndex != previousIndex) {
            currentIndex > previousIndex
        } else {
            currentOffset > previousScrollOffset
        }

        if (isScrollingDown) {
            isVisible = false
        } else {
            isVisible = true
        }

        previousIndex = currentIndex
        previousScrollOffset = currentOffset
    }
    return isVisible
}