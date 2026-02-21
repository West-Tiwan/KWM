package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser.*
import org.example.hotkey.HotkeyManager
import org.example.win.AppState
import org.example.win.WindowManager
import kotlin.concurrent.thread

class WindowApp {
    fun startAsync() {
        thread(start = true, isDaemon = false, name = "wm-thread") {
            start()
        }
    }


    fun start() {
        AppState.systemStats.start()

        val u32i = User32.INSTANCE
        val EVENT_SYSTEM_FOREGROUND = 0x0003
        val EVENT_OBJECT_CREATE = 0x8000
        val EVENT_OBJECT_DESTROY = 0x8001
        val WINEVENT_OUTOFCONTEXT = 0x0000
        val WINEVENT_SKIPOWNPROCESS = 0x0002
        val OBJID_WINDOW = 0
        val WS_EX_TOOLWINDOW = 0x00000080L

        val hkMg = HotkeyManager(u32i)
        val winMan = WindowManager()
        winMan.syncWindows()

        val proc = object: WinEventProc {
            override fun callback(
                hWinEventHook: HANDLE?,
                event: WinDef.DWORD?,
                hwnd: WinDef.HWND?,
                idObject: WinDef.LONG?,
                idChild: WinDef.LONG?,
                dwEventThread: WinDef.DWORD?,
                dwmsEventTime: WinDef.DWORD?
            ) {
                // 1. Basic null/child checks
                if (hwnd == null) return
                val title = CharArray(200)
                u32i.GetWindowText(hwnd, title, 20)
                val windowTitle = String(title).trim { it <= '\u0000' }
                if (windowTitle == "KWM") return
                if (idObject?.toInt() != OBJID_WINDOW) return
                if (idChild?.toInt() != 0) return

                // 2. Check Visibility
                // Many invisible utility windows fire creation events.
                if (!u32i.IsWindowVisible(hwnd)) return

                // 3. Check for Owner
                // If it has an owner, it's likely a dialog or modal, not a standalone window to tile.
                if (u32i.GetWindow(hwnd, DWORD(GW_OWNER.toLong())) != null) return

                // 4. Check Extended Styles (Tool Windows)
                // This catches floating toolbars and some custom tooltips.
                val exStyle = u32i.GetWindowLong(hwnd, GWL_EXSTYLE)
                if ((exStyle.toLong() and WS_EX_TOOLWINDOW) == WS_EX_TOOLWINDOW) return

                // 5. Check Class Names (Crucial for Menus)
                val classNameBuffer = CharArray(256)
                u32i.GetClassName(hwnd, classNameBuffer, 256)
                val className = String(classNameBuffer).trim { it <= ' ' } // Clean null terminators

                // #32768 = Standard Menus (Right click, etc)
                // tooltips_class32 = Tooltips
                // SysShadow = Dropdown shadows
                if (className == "#32768" ||
                    className == "tooltips_class32" ||
                    className == "SysShadow") return

                when (event?.toInt()) {
                    EVENT_OBJECT_DESTROY -> winMan.removeWindow(hwnd)
                    EVENT_OBJECT_CREATE -> winMan.syncWindows()
                    EVENT_SYSTEM_FOREGROUND -> winMan.syncWindows()
                }
            }
        }
        val hHook = u32i.SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_OBJECT_DESTROY, null, proc, 0, 0, WINEVENT_OUTOFCONTEXT or WINEVENT_SKIPOWNPROCESS)
        if (hHook == null) {
            println("failed to install hook")
        }
        println("hook listening")

        hkMg.bind()
        val msg = MSG()

        while (u32i.GetMessage(msg, null, 0, 0) > 0) {
            if (msg.message == WM_HOTKEY) {
                when (msg.wParam.toInt()) {
                    hkMg.HK_P -> winMan.promoteWindow(u32i.GetForegroundWindow())
                    hkMg.HK_D -> winMan.moveDownWindow(u32i.GetForegroundWindow())
                    hkMg.HK_U -> winMan.moveUpWindow(u32i.GetForegroundWindow())
                    hkMg.HK_W1 -> winMan.switchWorkspace(0)
                    hkMg.HK_W2 -> winMan.switchWorkspace(1)
                    hkMg.HK_W3 -> winMan.switchWorkspace(2)
                    hkMg.HK_W4 -> winMan.switchWorkspace(3)
                    hkMg.HK_W5 -> winMan.switchWorkspace(4)
                }
            }
        }
        u32i.UnhookWinEvent(hHook)
    }
}
