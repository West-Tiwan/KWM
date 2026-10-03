package org.example.platform.windows

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import org.example.layout.Rect
import org.example.win.MonitorBounds

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

    fun apply(hwnd: WinDef.HWND, rect: Rect) {
        u32i.ShowWindow(hwnd, WinUser.SW_RESTORE)
        u32i.SetWindowPos(
            hwnd,
            null,
            rect.x,
            rect.y,
            rect.w,
            rect.h,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE or WinUser.SWP_NOSENDCHANGING
        )
    }
}