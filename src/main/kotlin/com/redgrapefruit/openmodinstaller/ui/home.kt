package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.redgrapefruit.openmodinstaller.ui.components.GridModpackList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard() {
    GridModpackList(arrayOf("One", "Two", "Three"), arrayOf("One", "Two", "Three"))
}