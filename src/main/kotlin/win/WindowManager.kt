package org.example.win

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.example.layout.*

class WindowManager {
    val enum = EnumWindows()
    val u32i = User32.INSTANCE
    var managed = mutableListOf<WinDef.HWND>()
    var lastFocus: WinDef.HWND? = null
    val cont = WindowController(u32i)
    var dirty = false

    fun syncWindows() {
        val current = enum.getWindows().map { it.windowHandle }
        managed.removeIf { it !in current }
        current.forEach { 
            if (it !in managed) {
                managed.add(it)
                dirty = true
            }
        }
        if (dirty) {
            retile()
        }
    }
    
    fun onFocus(hwnd: WinDef.HWND) {
        if (hwnd in managed && hwnd != lastFocus) {
            managed.remove(hwnd)
            managed.add(0, hwnd)
            lastFocus = hwnd
            retile()
        }
    }


    fun retile() {
        dirty = false
        val list = managed.take(3)
        if (list.isEmpty()) return

        when (managed.size) {
            1 -> cont.apply(
                managed[0],
                FullScreen.rect(cont.getMonitorBounds(managed[0]))
            )
            2 -> {
                cont.apply(
                    managed[0],
                    LeftHalf.rect(cont.getMonitorBounds(managed[0]))
                )
                cont.apply(
                    managed[1],
                    RightHalf.rect(cont.getMonitorBounds(managed[1]))
                )
            }
            else -> {
                cont.apply(
                    managed[0],
                    LeftHalf.rect(cont.getMonitorBounds(managed[0]))
                )
                cont.apply(
                    managed[1],
                    TopHalf.rect(cont.getMonitorBounds(managed[1]))
                )
                cont.apply(
                    managed[2],
                    BottomHalf.rect(cont.getMonitorBounds(managed[2]))
                )
            }
        }
    }
}