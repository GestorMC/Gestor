package com.gestormc.gestor.ui.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

@Composable
fun getImageFromVersion(version: String): ImageBitmap {
    return when {
        version.startsWith("1.17") -> imageResource("drawable/mc_1.17.png")
        version.startsWith("1.16") -> imageResource("drawable/mc_1.16.png")
        else -> imageResource("drawable/mc.png")
    }
}

@Composable
fun getLayerTwoImageFromVersion(version: String): ImageBitmap {
    return when {
        version.startsWith("1.17") -> imageResource("drawable/layerTwo/mc_1.17.png")
        version.startsWith("1.16") -> imageResource("drawable/layerTwo/mc_1.16.png")
        else -> imageResource("drawable/layerTwo/mc.png")
    }
}
