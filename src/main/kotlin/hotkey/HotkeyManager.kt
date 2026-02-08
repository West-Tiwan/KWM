package org.example.hotkey

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.MOD_ALT
import com.sun.jna.platform.win32.Win32VK.*
import com.sun.jna.platform.win32.WinUser.MOD_NOREPEAT

class HotkeyManager (val u32i: User32) {
    val HK_P = 1
    val HK_U = 3
    val HK_D = 4

    fun bind() {
        u32i.RegisterHotKey(null, HK_P, MOD_ALT or MOD_NOREPEAT, VK_LEFT.code)
        u32i.RegisterHotKey(null, HK_U, MOD_ALT or MOD_NOREPEAT, VK_UP.code)
        u32i.RegisterHotKey(null, HK_D, MOD_ALT or MOD_NOREPEAT, VK_DOWN.code)
    }

    fun unbind() {
        u32i.UnregisterHotKey(null, HK_P)
        u32i.UnregisterHotKey(null, HK_U)
        u32i.UnregisterHotKey(null, HK_D)
    }
}