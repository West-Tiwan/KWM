package bar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.win.AppState
import java.awt.Toolkit
import kotlin.concurrent.thread

@Composable
fun BarContent() {
    val stats by AppState.systemStats.stats.collectAsState()
    val currentWs by AppState.workspaceManager.currentFlow.collectAsState()
    val cpuColor by animateColorAsState(
        targetValue = when {
            stats.cpu > 80 -> Color(0xFFFF4D4D)
            stats.cpu > 60 -> Color(0xFFFFA500)
            else -> Color.White
        },
        animationSpec = tween(300)
    )
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT — Workspaces
        Row {
            Box {
                Row {
                    for (i in 0 until 5) {
                        WorkspaceIndicator(
                            number = i + 1,
                            active = i == currentWs
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                }

                ActiveWorkspaceSlider(currentWs)
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
            Text("CPU ${stats.cpu}%", color = cpuColor, fontSize = 12.sp)
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
    val backgroundColor by animateColorAsState(
        targetValue =
            if (active) Color(0xFF3A86FF)
            else Color(0xFF2A2A2A),
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    )
    val scale by animateFloatAsState(
        targetValue = if (active) 1.1f else 1f,
        animationSpec = tween(250)
    )
    Box(
        modifier = Modifier
            .clickable {
                AppState.windowManager.switchWorkspace(number - 1)
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(20.dp)
            .background(
                backgroundColor,
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

@Composable
fun ActiveWorkspaceSlider(activeIndex: Int) {

    val indicatorWidth = 20.dp
    val spacing = 12.dp

    val offsetX by animateDpAsState(
        targetValue = activeIndex * (indicatorWidth + spacing),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .padding(top = 26.dp)
            .width(indicatorWidth)
            .height(2.dp)
            .background(
                Color(0xFF3A86FF),
                shape = RoundedCornerShape(1.dp)
            )
    )
}

class BarApp {
    private var exit: (() -> Unit)? = null
    fun startAsync() {
        thread(name = "bar-thread") {
            application {
                println("Compose application started")

                Window(
                    onCloseRequest = ::exitApplication,
                    undecorated = true,
                    resizable = false,
                    transparent = true,
                    alwaysOnTop = true,
                    title = "KWM",
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

                exit = ::exitApplication
            }
        }
    }
    fun shutdown() {
        exit?.invoke()
    }
}