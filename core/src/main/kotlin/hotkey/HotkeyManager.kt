package org.example.hotkey

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser.MOD_ALT
import com.sun.jna.platform.win32.Win32VK.*
import com.sun.jna.platform.win32.WinUser.MOD_NOREPEAT
import com.sun.jna.platform.win32.WinUser.MOD_SHIFT

class HotkeyManager (val u32i: User32) {
    val HK_P = 1
    val HK_U = 2
    val HK_D = 3
    val HK_W1 = 11
    val HK_W2 = 12
    val HK_W3 = 13
    val HK_W4 = 14
    val HK_W5 = 15
    val HK_MW1 = 21
    val HK_MW2 = 22
    val HK_MW3 = 23
    val HK_MW4 = 24
    val HK_MW5 = 25

    fun bind() {
        u32i.RegisterHotKey(null, HK_P, MOD_ALT or MOD_NOREPEAT, VK_LEFT.code)
        u32i.RegisterHotKey(null, HK_U, MOD_ALT or MOD_NOREPEAT, VK_UP.code)
        u32i.RegisterHotKey(null, HK_D, MOD_ALT or MOD_NOREPEAT, VK_DOWN.code)
        u32i.RegisterHotKey(null, HK_W1, MOD_ALT or MOD_NOREPEAT, VK_1.code)
        u32i.RegisterHotKey(null, HK_W2, MOD_ALT or MOD_NOREPEAT, VK_2.code)
        u32i.RegisterHotKey(null, HK_W3, MOD_ALT or MOD_NOREPEAT, VK_3.code)
        u32i.RegisterHotKey(null, HK_W4, MOD_ALT or MOD_NOREPEAT, VK_4.code)
        u32i.RegisterHotKey(null, HK_W5, MOD_ALT or MOD_NOREPEAT, VK_5.code)
        u32i.RegisterHotKey(null, HK_MW1, MOD_ALT or MOD_SHIFT or MOD_NOREPEAT, VK_1.code)
        u32i.RegisterHotKey(null, HK_MW2, MOD_ALT or MOD_SHIFT or MOD_NOREPEAT, VK_2.code)
        u32i.RegisterHotKey(null, HK_MW3, MOD_ALT or MOD_SHIFT or MOD_NOREPEAT, VK_3.code)
        u32i.RegisterHotKey(null, HK_MW4, MOD_ALT or MOD_SHIFT or MOD_NOREPEAT, VK_4.code)
        u32i.RegisterHotKey(null, HK_MW5, MOD_ALT or MOD_SHIFT or MOD_NOREPEAT, VK_5.code)
    }

    fun unbind() {
        u32i.UnregisterHotKey(null, HK_P)
        u32i.UnregisterHotKey(null, HK_U)
        u32i.UnregisterHotKey(null, HK_D)
        u32i.UnregisterHotKey(null, HK_W1)
        u32i.UnregisterHotKey(null, HK_W2)
        u32i.UnregisterHotKey(null, HK_W3)
        u32i.UnregisterHotKey(null, HK_W4)
        u32i.UnregisterHotKey(null, HK_W5)
        u32i.UnregisterHotKey(null, HK_MW1)
        u32i.UnregisterHotKey(null, HK_MW2)
        u32i.UnregisterHotKey(null, HK_MW3)
        u32i.UnregisterHotKey(null, HK_MW4)
        u32i.UnregisterHotKey(null, HK_MW5)
    }
}