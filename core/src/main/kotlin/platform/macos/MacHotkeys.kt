package org.example.platform.macos

import com.sun.jna.Pointer
import org.example.hotkey.Hotkey
import org.example.hotkey.HotkeyAction
import org.example.hotkey.Hotkeys
import org.example.hotkey.Key

/**
 * Global hotkeys through a keyboard event tap. Matching key presses are swallowed so the
 * focused app never sees them (otherwise Option+1 would also type "¡").
 */
class MacHotkeys(private val onAction: (HotkeyAction) -> Unit) {
    private val cg = CoreGraphics.INSTANCE
    private val cf = CoreFoundation.INSTANCE
    private var tap: Pointer? = null

    // Must stay strongly reachable for as long as the tap exists
    private val callback = object : CGEventTapCallBack {
        override fun invoke(proxy: Pointer?, type: Int, event: Pointer?, userInfo: Pointer?): Pointer? {
            if (type == TAP_DISABLED_BY_TIMEOUT || type == TAP_DISABLED_BY_USER_INPUT) {
                // macOS disables taps that respond too slowly; turn it straight back on
                tap?.let { cg.CGEventTapEnable(it, true) }
                return event
            }
            if (type != KEY_DOWN || event == null) return event

            val action = actionFor(event) ?: return event
            if (cg.CGEventGetIntegerValueField(event, KEYBOARD_EVENT_AUTOREPEAT) == 0L) {
                onAction(action)
            }
            return null
        }
    }

    /** Installs the tap on the calling thread's run loop. Events arrive while that run loop runs. */
    fun install() {
        val port = cg.CGEventTapCreate(SESSION_EVENT_TAP, HEAD_INSERT, TAP_OPTION_DEFAULT, 1L shl KEY_DOWN, callback, null)
            ?: error("Could not create the keyboard event tap. Is Accessibility access granted?")
        val source = cf.CFMachPortCreateRunLoopSource(null, port, 0)
        cf.CFRunLoopAddSource(cf.CFRunLoopGetCurrent(), source, CF.RUN_LOOP_COMMON_MODES)
        CF.release(source)
        cg.CGEventTapEnable(port, true)
        tap = port
    }

    fun uninstall() {
        tap?.let {
            cg.CGEventTapEnable(it, false)
            CF.release(it)
        }
        tap = null
    }

    private fun actionFor(event: Pointer): HotkeyAction? {
        val flags = cg.CGEventGetFlags(event)
        if (flags and FLAG_ALTERNATE == 0L) return null
        if (flags and (FLAG_COMMAND or FLAG_CONTROL) != 0L) return null
        val keyCode = cg.CGEventGetIntegerValueField(event, KEYBOARD_EVENT_KEYCODE).toInt()
        val key = keys[keyCode] ?: return null
        return Hotkeys.bindings[Hotkey(key, shift = flags and FLAG_SHIFT != 0L)]
    }

    private companion object {
        const val SESSION_EVENT_TAP = 1
        const val HEAD_INSERT = 0
        const val TAP_OPTION_DEFAULT = 0

        const val KEY_DOWN = 10
        const val TAP_DISABLED_BY_TIMEOUT = -2 // 0xFFFFFFFE
        const val TAP_DISABLED_BY_USER_INPUT = -1 // 0xFFFFFFFF

        const val KEYBOARD_EVENT_AUTOREPEAT = 8
        const val KEYBOARD_EVENT_KEYCODE = 9

        const val FLAG_SHIFT = 0x00020000L
        const val FLAG_CONTROL = 0x00040000L
        const val FLAG_ALTERNATE = 0x00080000L
        const val FLAG_COMMAND = 0x00100000L

        // Physical ANSI key codes (Carbon kVK_*), so bindings don't move with the keyboard layout
        val keys = mapOf(
            123 to Key.LEFT,
            126 to Key.UP,
            125 to Key.DOWN,
            18 to Key.NUM_1,
            19 to Key.NUM_2,
            20 to Key.NUM_3,
            21 to Key.NUM_4,
            23 to Key.NUM_5,
            12 to Key.Q
        )
    }
}
