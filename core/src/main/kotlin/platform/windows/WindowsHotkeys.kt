package org.example.platform.windows

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.Win32VK
import com.sun.jna.platform.win32.WinUser.MOD_ALT
import com.sun.jna.platform.win32.WinUser.MOD_NOREPEAT
import com.sun.jna.platform.win32.WinUser.MOD_SHIFT
import org.example.hotkey.HotkeyAction
import org.example.hotkey.Hotkeys
import org.example.hotkey.Key

class WindowsHotkeys(val u32i: User32) {
    // RegisterHotKey ids are positions in this list
    private val bindings = Hotkeys.bindings.entries.toList()

    fun bind() {
        bindings.forEachIndexed { id, (hotkey, _) ->
            val modifiers = MOD_ALT or MOD_NOREPEAT or (if (hotkey.shift) MOD_SHIFT else 0)
            u32i.RegisterHotKey(null, id, modifiers, virtualKey(hotkey.key))
        }
    }

    fun unbind() {
        bindings.indices.forEach { u32i.UnregisterHotKey(null, it) }
    }

    fun actionFor(id: Int): HotkeyAction? = bindings.getOrNull(id)?.value

    private fun virtualKey(key: Key) = when (key) {
        Key.LEFT -> Win32VK.VK_LEFT
        Key.UP -> Win32VK.VK_UP
        Key.DOWN -> Win32VK.VK_DOWN
        Key.NUM_1 -> Win32VK.VK_1
        Key.NUM_2 -> Win32VK.VK_2
        Key.NUM_3 -> Win32VK.VK_3
        Key.NUM_4 -> Win32VK.VK_4
        Key.NUM_5 -> Win32VK.VK_5
        Key.Q -> Win32VK.VK_Q
    }.code
}
