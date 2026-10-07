package org.example.platform.macos

import org.example.win.MonitorBounds
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

/**
 * AWT on macOS uses the same top-left-origin global coordinates as the AX API,
 * and its screen insets already exclude the menu bar and the Dock.
 */
object MacScreens {
    fun workArea(x: Double, y: Double): MonitorBounds {
        val environment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val config = environment.screenDevices
            .map { it.defaultConfiguration }
            .firstOrNull { it.bounds.contains(x, y) }
            ?: environment.defaultScreenDevice.defaultConfiguration
        return workArea(config)
    }

    private fun workArea(config: GraphicsConfiguration): MonitorBounds {
        val bounds = config.bounds
        val insets = Toolkit.getDefaultToolkit().getScreenInsets(config)
        return MonitorBounds(
            x = bounds.x + insets.left,
            y = bounds.y + insets.top,
            width = bounds.width - insets.left - insets.right,
            height = bounds.height - insets.top - insets.bottom
        )
    }
}
