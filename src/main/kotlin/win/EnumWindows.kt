package org.example.win

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.W32APIOptions

data class WindowInfo(
    val windowHandle: WinDef.HWND,
    val windowTitle: String
)

interface Dwmapi : com.sun.jna.Library {
    fun DwmGetWindowAttribute(hwnd: WinDef.HWND, dwAttribute: Int, pvAttribute: IntByReference, cbAttribute: Int): Int
    companion object {
        val INSTANCE = Native.load("dwmapi", Dwmapi::class.java, W32APIOptions.DEFAULT_OPTIONS) as Dwmapi
        const val DWMWA_CLOAKED = 14
    }
}

class EnumWindows {
    val u32i = User32.INSTANCE
    val myPid = ProcessHandle.current().pid()
    val GWL_STYLE = -16
    val GWL_EXSTYLE = -20
    val WS_THICKFRAME = 0x00040000 // Resizable
    val WS_EX_TOOLWINDOW = 0x00000080 // Tool window

    fun getWindows(): List<WindowInfo> {
        val windowList = mutableListOf<WindowInfo>()
        u32i.EnumWindows(object: WNDENUMPROC {
            override fun callback(p0: WinDef.HWND?, p1: Pointer?): Boolean {
                if (!u32i.IsWindowVisible(p0)) return true

                val cloaked = IntByReference()
                Dwmapi.INSTANCE.DwmGetWindowAttribute(p0!!, Dwmapi.DWMWA_CLOAKED, cloaked, 4)
                if (cloaked.value != 0) return true

                val exStyle = u32i.GetWindowLong(p0, GWL_EXSTYLE)
                if ((exStyle and WS_EX_TOOLWINDOW) != 0) return true

                val rect = WinDef.RECT()
                u32i.GetWindowRect(p0, rect)
                if (rect.left == rect.right || rect.top == rect.bottom) return true

                val pidRef = IntByReference()
                u32i.GetWindowThreadProcessId(p0, pidRef)
                if (pidRef.value.toLong() == myPid) return true

                val titleBuffer = CharArray(1024)
                u32i.GetWindowText(p0, titleBuffer, 1024)
                val title = String(titleBuffer).trim { it <= '\u0000' }

                val style = u32i.GetWindowLong(p0, GWL_STYLE)
                val isResizable = (style and WS_THICKFRAME) != 0

                if (title.isNotEmpty() && isResizable) {
                    windowList.add(WindowInfo(
                        p0,
                        title
                    ))
                }
                return true
            }
        }, null)

        return windowList
    }
}