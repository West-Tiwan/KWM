package org.example.win

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser

class WindowController (val u32i: User32) {

    fun getMonitorBounds(hwnd: WinDef.HWND): MonitorBounds {
        val monitor = u32i.MonitorFromWindow(hwnd, WinUser.MONITOR_DEFAULTTONEAREST)
        val info = WinUser.MONITORINFO()
        info.cbSize = info.size()
        u32i.GetMonitorInfo(monitor, info)
        val r = info.rcWork // excludes taskbar
        return MonitorBounds(
            x = r.left,
            y = r.top,
            width = r.right - r.left,
            height = r.bottom - r.top
        )
    }

    fun snapLeft(hwnd: WinDef.HWND) {
        val m = getMonitorBounds(hwnd)
        u32i.SetWindowPos(
            hwnd,
            null,
            m.x,
            m.y,
            m.width/2,
            m.height,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE or WinUser.SWP_NOSENDCHANGING
        )
    }
    fun snapRight(hwnd: WinDef.HWND) {
        val m = getMonitorBounds(hwnd)
        u32i.SetWindowPos(
            hwnd,
            null,
            m.width/2,
            m.y,
            m.width/2,
            m.height,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE or WinUser.SWP_NOSENDCHANGING
        )
    }
    fun snapTop(hwnd: WinDef.HWND) {
        val m = getMonitorBounds(hwnd)
        u32i.SetWindowPos(
            hwnd,
            null,
            m.x,
            m.y,
            m.width,
            m.height/2,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE or WinUser.SWP_NOSENDCHANGING
        )
    }
    fun snapBottom(hwnd: WinDef.HWND) {
        val m = getMonitorBounds(hwnd)
        u32i.SetWindowPos(
            hwnd,
            null,
            m.x,
            m.height/2,
            m.width,
            m.height/2,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE or WinUser.SWP_NOSENDCHANGING
        )
    }
}