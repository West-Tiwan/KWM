package org.example.win

import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.W32APIOptions
import org.example.layout.*
import kotlin.concurrent.thread

interface MyUser32 : User32 {
    companion object {
        val INSTANCE: MyUser32 = Native.load("user32", MyUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun WindowFromPoint(point: WinDef.POINT.ByValue): WinDef.HWND?
}

class WindowManager {
    val enum = EnumWindows()
    val u32i = MyUser32.INSTANCE
    val workspaceManager = AppState.workspaceManager
    val cont = WindowController(u32i)

    fun syncWindows() {
        val currentSystemWindows = enum.getWindows().map { it.windowHandle }
        val workspaceWindows = workspaceManager.currentWindows()

        currentSystemWindows.forEach {
            if (it !in workspaceWindows) {
                workspaceManager.addWindow(it)
            }
        }

        val toRemove = workspaceWindows.filter { it !in currentSystemWindows }

        toRemove.forEach {
            workspaceManager.removeWindow(it)
        }

        retile()
    }

    fun windowUnderCursor(): WinDef.HWND? {
        val point = WinDef.POINT()
        u32i.GetCursorPos(point)
        val byVal = WinDef.POINT.ByValue()
        byVal.x = point.x
        byVal.y = point.y
        return u32i.WindowFromPoint(byVal)
    }

    fun topLevel(hwnd: WinDef.HWND?): WinDef.HWND? {
        if (hwnd == null) return null
        return u32i.GetAncestor(hwnd, User32.GA_ROOT)
    }

    fun focusWindow(hwnd: WinDef.HWND?) {
        if (hwnd == null) return

        val currentThreadId = WinDef.DWORD(Kernel32.INSTANCE.GetCurrentThreadId().toLong())
        val targetThreadId = WinDef.DWORD(Kernel32.INSTANCE.GetCurrentThreadId().toLong())

        u32i.AttachThreadInput(currentThreadId, targetThreadId, true)

        u32i.ShowWindow(hwnd, User32.SW_RESTORE)
        u32i.SetForegroundWindow(hwnd)
        u32i.SetFocus(hwnd)

        u32i.AttachThreadInput(currentThreadId, targetThreadId, false)
    }

    fun startFocusFollowsMouse() {
        thread(name = "ffm-thread", isDaemon = true) {
            var lastFocused: WinDef.HWND? = null

            while (true) {
                val raw = windowUnderCursor()
                val top = topLevel(raw)

                if (top != null &&
                    top != lastFocused &&
                    top in workspaceManager.currentWindows()
                ) {
                    focusWindow(top)
                    lastFocused = top
                }
                Thread.sleep(100)
            }
        }
    }

    fun removeWindow(hwnd: WinDef.HWND) {
        workspaceManager.removeWindow(hwnd)
        retile()
    }

    fun switchWorkspace(index: Int) {
        val (old, new) = workspaceManager.switchTo(index)
        old.forEach { u32i.ShowWindow(it, User32.SW_HIDE) }
        new.forEach { u32i.ShowWindow(it, User32.SW_RESTORE) }
        retile()
    }

    fun moveWinToWs(index: Int, hwnd: WinDef.HWND) {
        val list = workspaceManager.currentWindows()
        if (hwnd !in list) return
        workspaceManager.removeWindow(hwnd)
        switchWorkspace(index)
        workspaceManager.addWindow(hwnd)
        retile()
    }

    fun promoteWindow(hwnd: WinDef.HWND) {
        val list = workspaceManager.currentWindows()
        if (hwnd !in list) return
        workspaceManager.removeWindow(hwnd)
        workspaceManager.addWindow(hwnd, 0)
        retile()
        println("Promoted")
    }

    fun moveDownWindow(hwnd: WinDef.HWND) {
        val curr = workspaceManager.currentWindows().indexOf(hwnd)
        if (curr == -1) return
        if (curr != workspaceManager.currentWindows().size - 1) {
            val next = curr + 1
            workspaceManager.swapWindows(curr, next)
            retile()
            println("moved Down")
        }
    }

    fun moveUpWindow(hwnd: WinDef.HWND) {
        val curr = workspaceManager.currentWindows().indexOf(hwnd)
        if (curr <= 0) return
        val prev = curr - 1
        workspaceManager.swapWindows(curr, prev)
        retile()
        println("moved Up")
    }

    fun retile() {
        val list = workspaceManager.currentWindows().take(4)
        if (list.isEmpty()) return
        when (list.size) {
            1 -> cont.apply(
                list[0],
                FullScreen.rect(cont.getMonitorBounds(list[0]))
            )
            2 -> {
                cont.apply(
                    list[0],
                    LeftHalf.rect(cont.getMonitorBounds(list[0]))
                )
                cont.apply(
                    list[1],
                    RightHalf.rect(cont.getMonitorBounds(list[1]))
                )
            }
            3 -> {
                cont.apply(
                    list[0],
                    LeftHalf.rect(cont.getMonitorBounds(list[0]))
                )
                cont.apply(
                    list[1],
                    TopHalf.rect(cont.getMonitorBounds(list[1]))
                )
                cont.apply(
                    list[2],
                    BottomHalf.rect(cont.getMonitorBounds(list[2]))
                )
            }
            else -> {
                cont.apply(
                    list[0],
                    LeftHalf.rect(cont.getMonitorBounds(list[0]))
                )
                cont.apply(
                    list[1],
                    RightTopThird.rect(cont.getMonitorBounds(list[1]))
                )
                cont.apply(
                    list[2],
                    RightMiddleThird.rect(cont.getMonitorBounds(list[2]))
                )
                cont.apply(
                    list[3],
                    RightBottomThird.rect(cont.getMonitorBounds(list[3]))
                )
            }
        }
    }
}