package org.example.platform.macos

import com.sun.jna.Pointer
import org.example.platform.WindowRef
import java.lang.ref.Cleaner

/**
 * An AX window element. Every AX query hands back a fresh element for the same window,
 * so equality goes through CFEqual instead of comparing pointers.
 */
class MacWindow(element: Pointer, val pid: Int) : WindowRef {
    val element: Pointer = CF.retain(element)

    init {
        cleaner.register(this, Release(this.element))
    }

    override fun equals(other: Any?) = other is MacWindow && CF.equal(element, other.element)

    override fun hashCode() = CF.hash(element).hashCode()

    override fun toString() = "MacWindow(pid=$pid)"

    // Must not capture the MacWindow itself, or it would never become unreachable
    private class Release(private val ref: Pointer) : Runnable {
        override fun run() = CF.release(ref)
    }

    companion object {
        private val cleaner = Cleaner.create()
    }
}
