package bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.win.AppState
import java.awt.Toolkit

@Composable
fun BarContent() {
    val stats by AppState.systemStats.stats.collectAsState()
    val currentWs by AppState.workspaceManager.currentFlow.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT — Workspaces
        Row {
            for (i in 0 until 5) {
                WorkspaceIndicator(
                    number = i + 1,
                    active = i == currentWs
                )
                Spacer(Modifier.width(5.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        // CENTER — Date & Time
        Text(
            "${stats.date}   ${stats.time}",
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer(Modifier.weight(1f))

        // RIGHT — System Info
        Row {
            Text("CPU ${stats.cpu}%", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.width(12.dp))
            Text("RAM ${stats.ram}%", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.width(12.dp))
            if (stats.battery >= 0)
                Text("BAT ${stats.battery}%", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun WorkspaceIndicator(number: Int, active: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(
                if (active) Color(0xFF3A86FF)
                else Color(0xFF2A2A2A),
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            number.toString(),
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

class BarApp {
    fun start() = application {
        println("Compose application started")

        Window(
            onCloseRequest = ::exitApplication,
            undecorated = true,
            resizable = false,
            transparent = true,
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
            BarContent()
        }
    }
}