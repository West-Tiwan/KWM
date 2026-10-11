package org.example.hotkey

import kotlin.test.Test
import kotlin.test.assertEquals

class HotkeysTest {
    private val bindings = Hotkeys.bindings

    @Test
    fun `every workspace can be switched to and moved to`() {
        val numbers = listOf(Key.NUM_1, Key.NUM_2, Key.NUM_3, Key.NUM_4, Key.NUM_5)
        numbers.forEachIndexed { index, key ->
            assertEquals(HotkeyAction.SwitchWorkspace(index), bindings[Hotkey(key)])
            assertEquals(HotkeyAction.MoveToWorkspace(index), bindings[Hotkey(key, shift = true)])
        }
    }

    @Test
    fun `window ordering and quit keys`() {
        assertEquals(HotkeyAction.Promote, bindings[Hotkey(Key.LEFT)])
        assertEquals(HotkeyAction.MoveUp, bindings[Hotkey(Key.UP)])
        assertEquals(HotkeyAction.MoveDown, bindings[Hotkey(Key.DOWN)])
        assertEquals(HotkeyAction.Quit, bindings[Hotkey(Key.Q, shift = true)])
        assertEquals(null, bindings[Hotkey(Key.Q)])
    }
}
