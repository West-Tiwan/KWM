package org.example.win

import com.sun.jna.platform.win32.WinDef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Collections.swap

class WorkspaceManager (
    private val workspaceCount: Int
) {
    private val workspaces = List(workspaceCount) {mutableListOf<WinDef.HWND>()}
    var current = 0
        private set
    private val _currentFlow = MutableStateFlow(current)
    val currentFlow: StateFlow<Int> = _currentFlow

    fun currentWindows(): MutableList<WinDef.HWND> {
        return workspaces[current]
    }

    fun addWindow(hwnd: WinDef.HWND, index: Int = workspaces[current].size) {
        if (hwnd !in workspaces[current]) {
            workspaces[current].add(index, hwnd)
        }
    }

    fun removeWindow(hwnd: WinDef.HWND) {
        workspaces.forEach { it.remove(hwnd) }
    }

    fun swapWindows(curr: Int, next: Int) {
        swap(workspaces[current], curr, next)
    }

    fun switchTo(to: Int): Pair<List<WinDef.HWND>, List<WinDef.HWND>> {
        if (to !in workspaces.indices) return emptyList<WinDef.HWND>() to emptyList()
        val oldWindows = workspaces[current].toList()
        current = to
        _currentFlow.value = current
        val newWindows = workspaces[current].toList()
        return oldWindows to newWindows
    }

}