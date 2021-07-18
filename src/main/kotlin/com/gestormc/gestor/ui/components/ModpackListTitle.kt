@file:Suppress("FunctionName")

package com.gestormc.gestor.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun ModPackListTitle(titleText: String) {
    Row(Modifier.padding(horizontal = 21.dp)) {
        Text(titleText, fontSize = 1.7.em, fontWeight = FontWeight.Black)
    }
    Spacer(Modifier.padding(bottom = 8.dp))
}

@Composable
fun ModPackListTitleNoPadding(titleText: String) {
    Row {
        Text(titleText, fontSize = 1.7.em, fontWeight = FontWeight.Black)
    }
    Spacer(Modifier.padding(bottom = 8.dp))
}