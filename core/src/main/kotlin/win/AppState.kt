package org.example.win

object AppState {
    lateinit var windowManager: WindowManager
    val workspaceManager = WorkspaceManager(5)
    val systemStats = SystemStatService()
}