package org.example.platform

import org.example.hotkey.HotkeyAction
import org.example.layout.Rect
import org.example.platform.windows.WindowsPlatform
import org.example.win.MonitorBounds

/** A top-level window owned by the OS. Implementations need value equality so workspaces can track them. */
interface WindowRef

interface PlatformEvents {
    fun windowsChanged()
    fun windowClosed(window: WindowRef)
    fun hotkey(action: HotkeyAction)
}

/** Everything the window manager needs from the OS. */
interface Platform {
    fun windows(): List<WindowRef>
    fun monitorBounds(window: WindowRef): MonitorBounds
    fun setFrame(window: WindowRef, rect: Rect)
    fun hide(window: WindowRef)
    fun show(window: WindowRef)
    fun focus(window: WindowRef)
    fun focusedWindow(): WindowRef?
    fun windowUnderCursor(): WindowRef?

    /** Blocks the calling thread and delivers window and hotkey events until [stop] is called. */
    fun run(events: PlatformEvents)
    fun stop()

    companion object {
        fun create(): Platform = when (OperatingSystem.current) {
            OperatingSystem.WINDOWS -> WindowsPlatform()
            OperatingSystem.MACOS -> throw UnsupportedOperationException("macOS is not supported yet")
        }
    }
}
