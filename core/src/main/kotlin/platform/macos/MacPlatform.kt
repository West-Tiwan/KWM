package org.example.platform.macos

import org.example.layout.Rect
import org.example.platform.Platform
import org.example.platform.PlatformEvents
import org.example.platform.WindowRef
import org.example.win.MonitorBounds
import java.util.concurrent.ConcurrentHashMap

class MacPlatform : Platform {
    private val POLL_INTERVAL_MS = 250L

    @Volatile
    private var running = false

    // Where each hidden window was before it got parked
    private val parked = ConcurrentHashMap<MacWindow, Pair<Double, Double>>()

    init {
        MacPermissions.awaitAccessibility()
        AX.setTimeout(0.5f)
    }

    override fun windows(): List<WindowRef> = MacWindows.tileableWindows()

    override fun monitorBounds(window: WindowRef): MonitorBounds {
        val element = window.element
        val (x, y) = AX.position(element) ?: (0.0 to 0.0)
        val (w, h) = AX.size(element) ?: (0.0 to 0.0)
        return MacScreens.workArea(x + w / 2, y + h / 2)
    }

    override fun setFrame(window: WindowRef, rect: Rect) {
        val element = window.element
        // Shrink first so the move isn't blocked by the screen edge, then resize again
        // because some apps clamp their size to the screen while being moved
        AX.setSize(element, rect.w.toDouble(), rect.h.toDouble())
        AX.setPosition(element, rect.x.toDouble(), rect.y.toDouble())
        AX.setSize(element, rect.w.toDouble(), rect.h.toDouble())
    }

    /**
     * macOS can't hide a single window, and minimizing animates through the Dock, so park it
     * in the screen corner instead. The OS keeps a sliver visible, which is fine for a corner.
     */
    override fun hide(window: WindowRef) {
        val element = window.element
        val position = AX.position(element) ?: return
        parked[window as MacWindow] = position
        val screen = monitorBounds(window)
        AX.setPosition(element, (screen.x + screen.width - 1).toDouble(), (screen.y + screen.height - 1).toDouble())
    }

    override fun show(window: WindowRef) {
        val (x, y) = parked.remove(window) ?: return
        AX.setPosition(window.element, x, y)
    }

    override fun focus(window: WindowRef) {
        window as MacWindow
        val app = AX.copyApplication(window.pid)
        try {
            AX.setTrue(app, AX.FRONTMOST)
        } finally {
            CF.release(app)
        }
        AX.setTrue(window.element, AX.MAIN)
        AX.perform(window.element, AX.RAISE)
    }

    override fun focusedWindow(): WindowRef? {
        val app = AX.copy(AX.systemWide, AX.FOCUSED_APPLICATION) ?: return null
        try {
            val pid = AX.pid(app) ?: return null
            val window = AX.copy(app, AX.FOCUSED_WINDOW) ?: return null
            try {
                return MacWindow(window, pid)
            } finally {
                CF.release(window)
            }
        } finally {
            CF.release(app)
        }
    }

    // TODO: focus follows mouse
    override fun windowUnderCursor(): WindowRef? = null

    /** macOS has no cheap equivalent of SetWinEventHook for every app, so poll for window changes. */
    override fun run(events: PlatformEvents) {
        running = true
        var known = windows().toSet()
        while (running) {
            Thread.sleep(POLL_INTERVAL_MS)
            val current = windows().toSet()
            if (current != known) {
                known = current
                events.windowsChanged()
            }
        }
    }

    override fun stop() {
        running = false
    }

    private val WindowRef.element get() = (this as MacWindow).element
}
