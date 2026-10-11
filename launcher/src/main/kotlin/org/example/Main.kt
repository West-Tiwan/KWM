package org.example

import bar.BarApp
import org.example.platform.OperatingSystem

fun main() {
    println("Starting KWM on ${OperatingSystem.current}")

    if (OperatingSystem.current == OperatingSystem.MACOS) {
        // Run as a background agent: no Dock icon or app menu. Must be set before AWT starts.
        System.setProperty("apple.awt.UIElement", "true")
    }

    val wm = WindowApp()
    val bar = BarApp()

    wm.startAsync()
    bar.startAsync()

    wm.setOnShutdown {
        bar.shutdown()
    }
}