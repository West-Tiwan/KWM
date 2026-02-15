package bar

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Toolkit

class BarApp {
    fun start() = application {
        println("Compose application started")

        Window(
            onCloseRequest = ::exitApplication,
            undecorated = true,
            alwaysOnTop = true,
            state = rememberWindowState(
                width = Toolkit.getDefaultToolkit().screenSize.width.dp,
                height = 28.dp,
                position = WindowPosition.Absolute(
                    x = 0.dp,
                    y = 0.dp
                )
            )
        ) {
            println("Inside window content")
        }
    }
}