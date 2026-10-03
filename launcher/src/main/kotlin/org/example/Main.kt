package org.example

import bar.BarApp
import org.example.platform.OperatingSystem

fun main() {
    println("Starting KWM on ${OperatingSystem.current}")

    val wm = WindowApp()
    val bar = BarApp()

    wm.startAsync()
    bar.startAsync()

    wm.setOnShutdown {
        bar.shutdown()
    }
}