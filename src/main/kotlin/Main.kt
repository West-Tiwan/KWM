package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.*
import org.example.hotkey.HotkeyManager
import org.example.layout.*
import org.example.win.EnumWindows
import org.example.win.WindowController


fun main() {
    val u32i = User32.INSTANCE

    val cont = WindowController(u32i)
    val hkMg = HotkeyManager(u32i)
    val enumWindows = EnumWindows()

    hkMg.bind()
    val msg = MSG()

    while (u32i.GetMessage(msg, null, 0, 0) > 0) {
        val hwnd = enumWindows.getWindows()
        println(hwnd[0].windowTitle)

        if (msg.message == WM_HOTKEY) {
            when (msg.wParam.toInt()) {
                hkMg.HK_SL -> cont.apply(hwnd[0].windowHandle, LeftHalf.rect(cont.getMonitorBounds(hwnd[0].windowHandle)))
                hkMg.HK_SR -> cont.apply(hwnd[0].windowHandle, RightHalf.rect(cont.getMonitorBounds(hwnd[0].windowHandle)))
                hkMg.HK_ST -> cont.apply(hwnd[0].windowHandle, TopHalf.rect(cont.getMonitorBounds(hwnd[0].windowHandle)))
                hkMg.HK_SB -> cont.apply(hwnd[0].windowHandle, BottomHalf.rect(cont.getMonitorBounds(hwnd[0].windowHandle)))
            }
        }
    }
}