package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.*
import org.example.hotkey.HotkeyManager
import org.example.win.WindowManager


fun main() {
    val u32i = User32.INSTANCE

    val hkMg = HotkeyManager(u32i)
    val winMan = WindowManager()

    hkMg.bind()
    val msg = MSG()

    while (u32i.GetMessage(msg, null, 0, 0) > 0) {
        if (msg.message == WM_HOTKEY) {
            winMan.retile()
        }
    }
}