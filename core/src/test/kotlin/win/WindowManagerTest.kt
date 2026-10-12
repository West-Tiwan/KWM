package org.example.win

import org.example.hotkey.HotkeyAction
import org.example.layout.Rect
import org.example.platform.Platform
import org.example.platform.PlatformEvents
import org.example.platform.WindowRef
import kotlin.test.Test
import kotlin.test.assertEquals

private data class TestWindow(val id: Int) : WindowRef

private class RecordingPlatform : Platform {
    val shown = mutableListOf<WindowRef>()

    override fun windows() = emptyList<WindowRef>()
    override fun monitorBounds(window: WindowRef) = MonitorBounds(0, 0, 1920, 1080)
    override fun setFrame(window: WindowRef, rect: Rect) {}
    override fun hide(window: WindowRef) {}
    override fun show(window: WindowRef) {
        shown += window
    }
    override fun focus(window: WindowRef) {}
    override fun focusedWindow(): WindowRef? = null
    override fun windowUnderCursor(): WindowRef? = null
    override fun run(events: PlatformEvents) {}
    override fun stop() {}
}

class WindowManagerTest {
    @Test
    fun `quitting shows windows from every workspace exactly once`() {
        val platform = RecordingPlatform()
        val manager = WindowManager(platform)
        val workspaces = manager.workspaceManager
        val visible = TestWindow(1)
        val hidden = TestWindow(2)

        workspaces.addWindow(visible)
        workspaces.switchTo(1)
        workspaces.addWindow(hidden)
        workspaces.switchTo(0)

        // shutdown() and the JVM shutdown hook both call this
        manager.restoreAllWindows()
        manager.restoreAllWindows()

        assertEquals(listOf<WindowRef>(visible, hidden), platform.shown)
    }
}
