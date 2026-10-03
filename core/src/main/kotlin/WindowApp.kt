package org.example

import org.example.hotkey.HotkeyAction
import org.example.platform.Platform
import org.example.platform.PlatformEvents
import org.example.platform.WindowRef
import org.example.win.AppState
import org.example.win.WindowManager
import kotlin.concurrent.thread

class WindowApp {
    val platform = Platform.create()
    val winMan = WindowManager(platform)

    private val events = object : PlatformEvents {
        override fun windowsChanged() = winMan.syncWindows()
        override fun windowClosed(window: WindowRef) = winMan.removeWindow(window)
        override fun hotkey(action: HotkeyAction) = handle(action)
    }

    private var onShutdown: (() -> Unit)? = null

    fun setOnShutdown(callback: () -> Unit) {
        onShutdown = callback
    }

    fun startAsync() {
        thread(start = true, isDaemon = false, name = "wm-thread") {
            start()
        }
    }

    fun shutdown() {
        println("Shutting down WM")

        platform.stop()

        onShutdown?.invoke()
    }


    fun start() {
        AppState.systemStats.start()
        AppState.windowManager = winMan
        winMan.syncWindows()
        winMan.startFocusFollowsMouse()

        platform.run(events)
    }

    private fun handle(action: HotkeyAction) {
        when (action) {
            HotkeyAction.Quit -> shutdown()
            is HotkeyAction.SwitchWorkspace -> winMan.switchWorkspace(action.index)
            else -> {
                val focused = platform.focusedWindow() ?: return
                when (action) {
                    HotkeyAction.Promote -> winMan.promoteWindow(focused)
                    HotkeyAction.MoveUp -> winMan.moveUpWindow(focused)
                    HotkeyAction.MoveDown -> winMan.moveDownWindow(focused)
                    is HotkeyAction.MoveToWorkspace -> winMan.moveWinToWs(action.index, focused)
                    else -> Unit
                }
            }
        }
    }
}
