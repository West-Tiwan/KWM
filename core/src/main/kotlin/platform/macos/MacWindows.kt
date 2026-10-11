package org.example.platform.macos

import com.sun.jna.Pointer

object MacWindows {
    private const val ON_SCREEN_ONLY = 1
    private const val EXCLUDE_DESKTOP_ELEMENTS = 16
    private const val NORMAL_WINDOW_LAYER = 0L

    val ownPid = ProcessHandle.current().pid().toInt()
    private val OWNER_PID = CF.string("kCGWindowOwnerPID")
    private val LAYER = CF.string("kCGWindowLayer")
    private val BOUNDS = CF.string("kCGWindowBounds")
    private val X = CF.string("X")
    private val Y = CF.string("Y")
    private val WIDTH = CF.string("Width")
    private val HEIGHT = CF.string("Height")

    /** Apps with a normal window on screen, front to back. Doesn't need any permission. */
    fun appPids(): Set<Int> {
        val list = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(ON_SCREEN_ONLY or EXCLUDE_DESKTOP_ELEMENTS, 0)
            ?: return emptySet()
        try {
            return CF.toList(list).mapNotNullTo(LinkedHashSet()) { info ->
                val layer = CF.get(info, LAYER)?.let(CF::toLong)
                val pid = CF.get(info, OWNER_PID)?.let(CF::toLong)?.toInt()
                pid.takeIf { layer == NORMAL_WINDOW_LAYER && pid != ownPid }
            }
        } finally {
            CF.release(list)
        }
    }

    fun tileableWindows(): List<MacWindow> = appPids().flatMap(::windowsOf)

    /**
     * The app owning the frontmost normal window at a point, or null when KWM's own bar is there.
     * AX requests aimed at our own windows are answered in-process on the calling thread, and AWT
     * crashes if that isn't the AppKit thread, so they must never be made from the WM threads.
     */
    fun appAt(x: Double, y: Double): Int? {
        val list = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(ON_SCREEN_ONLY or EXCLUDE_DESKTOP_ELEMENTS, 0)
            ?: return null
        try {
            for (info in CF.toList(list)) {
                if (!contains(info, x, y)) continue
                val pid = CF.get(info, OWNER_PID)?.let(CF::toLong)?.toInt() ?: continue
                if (pid == ownPid) return null
                if (CF.get(info, LAYER)?.let(CF::toLong) == NORMAL_WINDOW_LAYER) return pid
            }
            return null
        } finally {
            CF.release(list)
        }
    }

    private fun contains(info: Pointer, x: Double, y: Double): Boolean {
        val bounds = CF.get(info, BOUNDS) ?: return false
        val left = CF.get(bounds, X)?.let(CF::toDouble) ?: return false
        val top = CF.get(bounds, Y)?.let(CF::toDouble) ?: return false
        val width = CF.get(bounds, WIDTH)?.let(CF::toDouble) ?: return false
        val height = CF.get(bounds, HEIGHT)?.let(CF::toDouble) ?: return false
        return x >= left && x < left + width && y >= top && y < top + height
    }

    private fun windowsOf(pid: Int): List<MacWindow> {
        val app = AX.copyApplication(pid)
        try {
            val windows = AX.copy(app, AX.WINDOWS) ?: return emptyList()
            try {
                return CF.toList(windows).filter(::isTileable).map { MacWindow(it, pid) }
            } finally {
                CF.release(windows)
            }
        } finally {
            CF.release(app)
        }
    }

    // Skips sheets, panels, popovers, minimized and native full screen windows
    private fun isTileable(window: Pointer) =
        AX.string(window, AX.ROLE) == "AXWindow" &&
            AX.string(window, AX.SUBROLE) == "AXStandardWindow" &&
            AX.bool(window, AX.MINIMIZED) != true &&
            AX.bool(window, AX.FULL_SCREEN) != true
}
