package com.gestormc.gestor

import androidx.compose.desktop.ComposePanel
import androidx.compose.runtime.Composable
import com.gestormc.gestor.launcher.GestorLauncher
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.SystemUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

val JSON = Json {
    ignoreUnknownKeys = true
    isLenient = true
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

    // adding ComposePanel on JFrame
    window.contentPane.add(composePanel, BorderLayout.CENTER)

    // setting the content
    composePanel.setContent { content() }
    window.minimumSize = Dimension(260, 100)
    window.setSize(800, 600)
    window.isVisible = true
}

fun main() {
    val launcher = GestorLauncher.forge("C:/Users/karpo/AppData/Roaming/.minecraft", isServer = false, testingLaunch = true)
    launcher.setup("1.16.5", true)
    launcher.launch(true, "MinecraftForge_Chad", 3000, "", "1.16.5", "release")

//    CWindow(title = "Gestor") {
//        MaterialTheme(colors = lightColors) {
//            DesktopTheme {
//                Surface(shape = RectangleShape, color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
//                    Dashboard()
//                }
//            }
//        }
//    }
}