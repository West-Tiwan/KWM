package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser

fun main() {
    val u32i = User32.INSTANCE
    val hwnd = u32i.GetForegroundWindow()

    val screenWidth = u32i.GetSystemMetrics(WinUser.SM_CXSCREEN)
    val screenHeight = u32i.GetSystemMetrics(WinUser.SM_CYSCREEN)

    u32i.SetWindowPos(
        hwnd,
        null,
        0,
        0,
        screenWidth/2,
        screenHeight,
        WinUser.SWP_NOZORDER
    )
}