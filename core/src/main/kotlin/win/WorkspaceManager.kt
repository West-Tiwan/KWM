package org.example.win

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.platform.WindowRef
import java.util.Collections.swap

class WorkspaceManager (
    private val workspaceCount: Int
) {
    private val workspaces = List(workspaceCount) {mutableListOf<WindowRef>()}
    var current = 0
        private set
    private val _currentFlow = MutableStateFlow(current)
    val currentFlow: StateFlow<Int> = _currentFlow

    fun currentWindows(): MutableList<WindowRef> {
        return workspaces[current]
    }

    fun allWindows(): List<WindowRef> {
        return workspaces.flatten()
    }

    fun isTracked(window: WindowRef): Boolean {
        return workspaces.any { window in it }
    }

    fun addWindow(window: WindowRef, index: Int = workspaces[current].size) {
        if (window !in workspaces[current]) {
            workspaces[current].add(index, window)
        }
    }

    fun removeWindow(window: WindowRef) {
        workspaces.forEach { it.remove(window) }
    }

    fun swapWindows(curr: Int, next: Int) {
        swap(workspaces[current], curr, next)
    }

    fun switchTo(to: Int): Pair<List<WindowRef>, List<WindowRef>> {
        if (to !in workspaces.indices) return emptyList<WindowRef>() to emptyList()
        val oldWindows = workspaces[current].toList()
        current = to
        _currentFlow.value = current
        val newWindows = workspaces[current].toList()
        return oldWindows to newWindows
    }

}
