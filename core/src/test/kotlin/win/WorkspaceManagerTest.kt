package org.example.win

import org.example.platform.WindowRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private data class FakeWindow(val id: Int) : WindowRef

class WorkspaceManagerTest {
    private val a = FakeWindow(1)
    private val b = FakeWindow(2)
    private val c = FakeWindow(3)

    @Test
    fun `adds each window to the current workspace once`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)
        workspaces.addWindow(b)
        workspaces.addWindow(a)
        assertEquals(listOf<WindowRef>(a, b), workspaces.currentWindows())
    }

    @Test
    fun `inserts at the given index`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)
        workspaces.addWindow(b)
        workspaces.addWindow(c, 0)
        assertEquals(listOf<WindowRef>(c, a, b), workspaces.currentWindows())
    }

    @Test
    fun `switching returns the windows to hide and show`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)

        assertEquals(listOf(a) to emptyList(), workspaces.switchTo(1))
        workspaces.addWindow(b)
        assertEquals(listOf(b) to listOf(a), workspaces.switchTo(0))
        assertEquals(0, workspaces.currentFlow.value)
    }

    @Test
    fun `ignores switching to a workspace that does not exist`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)
        assertEquals(emptyList<WindowRef>() to emptyList(), workspaces.switchTo(5))
        assertEquals(0, workspaces.current)
    }

    @Test
    fun `tracks windows on other workspaces`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)
        workspaces.switchTo(1)

        assertTrue(workspaces.isTracked(a))
        assertFalse(a in workspaces.currentWindows())

        workspaces.removeWindow(a)
        assertFalse(workspaces.isTracked(a))
    }

    @Test
    fun `swaps windows in the current workspace`() {
        val workspaces = WorkspaceManager(3)
        workspaces.addWindow(a)
        workspaces.addWindow(b)
        workspaces.swapWindows(0, 1)
        assertEquals(listOf<WindowRef>(b, a), workspaces.currentWindows())
    }
}
