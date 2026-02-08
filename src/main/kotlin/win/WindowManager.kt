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

    fun promoteWindow(hwnd: WinDef.HWND) {
        managed.remove(hwnd)
        managed.add(0, hwnd)
        lastFocus = hwnd
        retile()
        println("Promoted")
    }

    fun moveDownWindow(hwnd: WinDef.HWND) {
        val curr = managed.indexOf(hwnd)
        if (curr != managed.size - 1) {
            val next = curr + 1
            val temp = managed[curr]
            managed[curr] = managed[next]
            managed[next] = temp
            retile()
            println("moved Down")
        }
    }

    fun moveUpWindow(hwnd: WinDef.HWND) {
        val curr = managed.indexOf(hwnd)
        if (curr > 1) {
            val prev = curr - 1
            val temp = managed[curr]
            managed[curr] = managed[prev]
            managed[prev] = temp
            retile()
            println("moved Up")
        }
    }

    fun retile() {
        dirty = false
        val list = managed.take(4)
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
            3 -> {
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
            else -> {
                cont.apply(
                    managed[0],
                    LeftHalf.rect(cont.getMonitorBounds(managed[0]))
                )
                cont.apply(
                    managed[1],
                    RightTopThird.rect(cont.getMonitorBounds(managed[1]))
                )
                cont.apply(
                    managed[2],
                    RightMiddleThird.rect(cont.getMonitorBounds(managed[2]))
                )
                cont.apply(
                    managed[3],
                    RightBottomThird.rect(cont.getMonitorBounds(managed[3]))
                )
            }
        }
    }
}