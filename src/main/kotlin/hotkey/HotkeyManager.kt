package org.example.hotkey

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.MOD_ALT
import java.awt.event.KeyEvent.*

class HotkeyManager (val u32i: User32) {
    val HK_SL = 1
    val HK_SR = 2
    val HK_ST = 3
    val HK_SB = 4

    fun bind() {
        u32i.RegisterHotKey(null, HK_SL, MOD_ALT, VK_LEFT)
        u32i.RegisterHotKey(null, HK_SR, MOD_ALT, VK_RIGHT)
        u32i.RegisterHotKey(null, HK_ST, MOD_ALT, VK_UP)
        u32i.RegisterHotKey(null, HK_SB, MOD_ALT, VK_DOWN)
    }
}