package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.redgrapefruit.openmodinstaller.consts.exampleModPack
import com.redgrapefruit.openmodinstaller.consts.exampleModPackOld
import com.redgrapefruit.openmodinstaller.consts.exampleModPackTwo
import com.redgrapefruit.openmodinstaller.consts.exampleModPackWithCustomBackground
import com.redgrapefruit.openmodinstaller.ui.components.GridModpackList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard() {
    GridModpackList(
        arrayOf(exampleModPack, exampleModPackTwo, exampleModPackOld, exampleModPackWithCustomBackground),
        arrayOf(exampleModPack, exampleModPackTwo)
    )
}