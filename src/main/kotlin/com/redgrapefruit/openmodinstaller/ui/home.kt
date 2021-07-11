package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.redgrapefruit.openmodinstaller.consts.*
import com.redgrapefruit.openmodinstaller.ui.components.GridModpackList
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard() {
    GridModpackList(
        arrayOf(exampleModPack, exampleModPackTwo, exampleModPackOld, exampleModPackWithCustomBackground, exampleModPackWithCustomBackgroundFull),
        arrayOf(exampleModPack, exampleModPackTwo, exampleModPackWithCustomBackgroundFull)
    )
}