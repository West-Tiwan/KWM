package org.example.hotkey

sealed interface HotkeyAction {
    data object Promote : HotkeyAction
    data object MoveUp : HotkeyAction
    data object MoveDown : HotkeyAction
    data class SwitchWorkspace(val index: Int) : HotkeyAction
    data class MoveToWorkspace(val index: Int) : HotkeyAction
    data object Quit : HotkeyAction
}

enum class Key { LEFT, UP, DOWN, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, Q }

/** Every binding uses Alt (Option on macOS) as the base modifier. */
data class Hotkey(val key: Key, val shift: Boolean = false)

object Hotkeys {
    private val workspaceKeys = listOf(Key.NUM_1, Key.NUM_2, Key.NUM_3, Key.NUM_4, Key.NUM_5)

    val bindings: Map<Hotkey, HotkeyAction> = buildMap {
        put(Hotkey(Key.LEFT), HotkeyAction.Promote)
        put(Hotkey(Key.UP), HotkeyAction.MoveUp)
        put(Hotkey(Key.DOWN), HotkeyAction.MoveDown)
        workspaceKeys.forEachIndexed { index, key ->
            put(Hotkey(key), HotkeyAction.SwitchWorkspace(index))
            put(Hotkey(key, shift = true), HotkeyAction.MoveToWorkspace(index))
        }
        put(Hotkey(Key.Q, shift = true), HotkeyAction.Quit)
    }
}
