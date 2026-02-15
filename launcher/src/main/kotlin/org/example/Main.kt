package org.example

import bar.BarApp

fun main() {
    val wm = WindowApp()
    val bar = BarApp()

    wm.startAsync()
    bar.start()
}