package org.example.win

import com.sun.jna.platform.win32.User32
import org.example.layout.*

class WindowManager {
    val enum = EnumWindows()
    val u32i = User32.INSTANCE
    val cont = WindowController(u32i)

    fun retile() {
        val list = enum.getWindows()

        val focused = u32i.GetForegroundWindow()
        val ordered = list.sortedByDescending {
            it.windowHandle == focused
        }
        when (ordered.size) {
            1 -> cont.apply(
                ordered[0].windowHandle,
                FullScreen.rect(cont.getMonitorBounds(ordered[0].windowHandle))
            )
            2 -> {
                cont.apply(
                    ordered[0].windowHandle,
                    LeftHalf.rect(cont.getMonitorBounds(ordered[0].windowHandle))
                )
                cont.apply(
                    ordered[1].windowHandle,
                    RightHalf.rect(cont.getMonitorBounds(ordered[1].windowHandle))
                )
            }
            else -> {
                cont.apply(
                    ordered[0].windowHandle,
                    LeftHalf.rect(cont.getMonitorBounds(ordered[0].windowHandle))
                )
                cont.apply(
                    ordered[1].windowHandle,
                    RightHalf.rect(cont.getMonitorBounds(ordered[1].windowHandle))
                )
                cont.apply(
                    ordered[2].windowHandle,
                    BottomHalf.rect(cont.getMonitorBounds(ordered[2].windowHandle))
                )
            }
        }
    }
}