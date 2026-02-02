package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.*
import org.example.hotkey.HotkeyManager
import org.example.win.WindowController

fun main() {
    val u32i = User32.INSTANCE

    val cont = WindowController(u32i)
    val hkMg = HotkeyManager(u32i)

    hkMg.bind()
    val msg = MSG()

    while (u32i.GetMessage(msg, null, 0, 0) > 0) {
        val hwnd = u32i.GetForegroundWindow()

        if (msg.message == WM_HOTKEY) {
            when (msg.wParam.toInt()) {
                hkMg.HK_SL -> cont.snapLeft(hwnd)
                hkMg.HK_SR -> cont.snapRight(hwnd)
                hkMg.HK_ST -> cont.snapTop(hwnd)
                hkMg.HK_SB -> cont.snapBottom(hwnd)
            }
        }
    }
}