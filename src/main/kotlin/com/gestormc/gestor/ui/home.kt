package com.gestormc.gestor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.imageResource
import com.gestormc.gestor.const.*
import com.gestormc.gestor.ui.components.ActionBar
import com.gestormc.gestor.ui.components.GridModpackList

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