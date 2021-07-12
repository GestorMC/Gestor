package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.imageResource
import com.redgrapefruit.openmodinstaller.consts.*
import com.redgrapefruit.openmodinstaller.ui.components.ActionBar
import com.redgrapefruit.openmodinstaller.ui.components.GridModpackList

@Composable
fun Dashboard() {
    Column {
        ActionBar("Example User", imageResource("drawable/defaultHead.png"))
        GridModpackList (
            arrayOf(exampleModPack, exampleModPackTwo, exampleModPackOld, exampleModPackWithCustomBackground, exampleModPackWithCustomBackgroundFull),
            arrayOf(exampleModPack, exampleModPackTwo, exampleModPackWithCustomBackgroundFull)
        )
    }
}