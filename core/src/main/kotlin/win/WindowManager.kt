package org.example.win

import org.example.layout.*
import org.example.platform.Platform
import org.example.platform.WindowRef
import kotlin.concurrent.thread

class WindowManager(private val platform: Platform) {
    val workspaceManager = AppState.workspaceManager

    fun syncWindows() {
        val currentSystemWindows = platform.windows()
        val workspaceWindows = workspaceManager.currentWindows()

        currentSystemWindows.forEach {
            // windows parked on another workspace can still be reported by the OS
            if (!workspaceManager.isTracked(it)) {
                workspaceManager.addWindow(it)
            }
        }

        val toRemove = workspaceWindows.filter { it !in currentSystemWindows }

        toRemove.forEach {
            workspaceManager.removeWindow(it)
        }

        retile()
    }

    fun startFocusFollowsMouse() {
        thread(name = "ffm-thread", isDaemon = true) {
            var lastFocused: WindowRef? = null

            while (true) {
                val top = platform.windowUnderCursor()

                if (top != null &&
                    top != lastFocused &&
                    top in workspaceManager.currentWindows()
                ) {
                    platform.focus(top)
                    lastFocused = top
                }
                Thread.sleep(100)
            }
        }
    }

    fun removeWindow(window: WindowRef) {
        workspaceManager.removeWindow(window)
        retile()
    }

    fun switchWorkspace(index: Int) {
        val (old, new) = workspaceManager.switchTo(index)
        old.forEach { platform.hide(it) }
        new.forEach { platform.show(it) }
        retile()
    }

    fun moveWinToWs(index: Int, window: WindowRef) {
        val list = workspaceManager.currentWindows()
        if (window !in list) return
        workspaceManager.removeWindow(window)
        switchWorkspace(index)
        workspaceManager.addWindow(window)
        retile()
    }

    fun promoteWindow(window: WindowRef) {
        val list = workspaceManager.currentWindows()
        if (window !in list) return
        workspaceManager.removeWindow(window)
        workspaceManager.addWindow(window, 0)
        retile()
        println("Promoted")
    }

    fun moveDownWindow(window: WindowRef) {
        val curr = workspaceManager.currentWindows().indexOf(window)
        if (curr == -1) return
        if (curr != workspaceManager.currentWindows().size - 1) {
            val next = curr + 1
            workspaceManager.swapWindows(curr, next)
            retile()
            println("moved Down")
        }
    }

    fun moveUpWindow(window: WindowRef) {
        val curr = workspaceManager.currentWindows().indexOf(window)
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
            1 -> place(list[0], FullScreen)
            2 -> {
                place(list[0], LeftHalf)
                place(list[1], RightHalf)
            }
            3 -> {
                place(list[0], LeftHalf)
                place(list[1], TopHalf)
                place(list[2], BottomHalf)
            }
            else -> {
                place(list[0], LeftHalf)
                place(list[1], RightTopThird)
                place(list[2], RightMiddleThird)
                place(list[3], RightBottomThird)
            }
        }
    }

    private fun place(window: WindowRef, layout: Layout) {
        platform.setFrame(window, layout.rect(platform.monitorBounds(window)))
    }
}
