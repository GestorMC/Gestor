package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redgrapefruit.openmodinstaller.ui.Dashboard
import com.redgrapefruit.openmodinstaller.ui.lightColors

import kotlinx.serialization.json.Json
import org.apache.commons.lang3.SystemUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

val JSON = Json {
    ignoreUnknownKeys = true
}
fun CWindow(title: String? = null, content: @Composable () -> Unit) = SwingUtilities.invokeLater {
    val window = JFrame()

    // creating ComposePanel
    val composePanel = ComposePanel()
    val rootPane = window.rootPane;
    if (SystemUtils.IS_OS_MAC) {
        rootPane.putClientProperty("apple.awt.fullWindowContent", true)
        rootPane.putClientProperty("apple.awt.brushMetalLook", true)
        rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
    } else
    window.title = title;

    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

    // addind ComposePanel on JFrame
    window.contentPane.add(composePanel, BorderLayout.CENTER)

    // setting the content
    composePanel.setContent {
        content()
    }
    window.minimumSize = Dimension(260, 100)
    window.setSize(800, 600)
    window.setVisible(true)
}

fun main() {
    CWindow(title = "OpenModInstaller") {
        MaterialTheme(colors = lightColors) {
            DesktopTheme {
                Dashboard()
            }
        }
    }
}