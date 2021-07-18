package com.gestormc.gestor.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActionBar(username: String, icon: ImageBitmap) {
    val (value, setValue) = remember { mutableStateOf(false) }
    Dialog(
        visible = value,
        onCloseRequest = { setValue(false) },
        resizable = false,
        title = "Preferences",
    ) {
        Button(
            modifier = Modifier.padding(15.dp),
            onClick = { setValue(false) }
        ) {
            Text("Nothing to see here")
        }
    }
    Row (Modifier.padding(horizontal = 21.dp).padding(top = 15.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface( shape = RoundedCornerShape(12f)) {
            Row(Modifier.height(28.dp).focusable().clickable {
                setValue(true)
            }, verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(6.dp))
                Text(username, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.size(6.dp))
                Image(icon, "", Modifier.clip(RoundedCornerShape(4f)).size(16.dp))
                Spacer(Modifier.size(6.dp))
            }
        }
    }
}