package org.example.hotkey

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.MOD_ALT
import com.sun.jna.platform.win32.Win32VK.*
import com.sun.jna.platform.win32.WinUser.MOD_NOREPEAT

class HotkeyManager (val u32i: User32) {
    val HK_SL = 1
    val HK_SR = 2
    val HK_ST = 3
    val HK_SB = 4

    fun bind() {
        u32i.RegisterHotKey(null, HK_SL, MOD_ALT or MOD_NOREPEAT, VK_LEFT.code)
        u32i.RegisterHotKey(null, HK_SR, MOD_ALT or MOD_NOREPEAT, VK_RIGHT.code)
        u32i.RegisterHotKey(null, HK_ST, MOD_ALT or MOD_NOREPEAT, VK_UP.code)
        u32i.RegisterHotKey(null, HK_SB, MOD_ALT or MOD_NOREPEAT, VK_DOWN.code)
    }

    fun unbind() {
        u32i.UnregisterHotKey(null, HK_SL)
        u32i.UnregisterHotKey(null, HK_SR)
        u32i.UnregisterHotKey(null, HK_ST)
        u32i.UnregisterHotKey(null, HK_SB)
    }
}