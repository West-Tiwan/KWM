package org.example.platform.macos

import com.sun.jna.Pointer

object MacWindows {
    private const val ON_SCREEN_ONLY = 1
    private const val EXCLUDE_DESKTOP_ELEMENTS = 16
    private const val NORMAL_WINDOW_LAYER = 0L

    private val ownPid = ProcessHandle.current().pid().toInt()
    private val OWNER_PID = CF.string("kCGWindowOwnerPID")
    private val LAYER = CF.string("kCGWindowLayer")

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
