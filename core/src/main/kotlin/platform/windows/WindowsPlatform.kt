package org.example.platform.windows

import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser.*
import com.sun.jna.win32.W32APIOptions
import org.example.layout.Rect
import org.example.platform.Platform
import org.example.platform.PlatformEvents
import org.example.platform.WindowRef
import org.example.win.MonitorBounds

interface MyUser32 : User32 {
    companion object {
        val INSTANCE: MyUser32 = Native.load("user32", MyUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun WindowFromPoint(point: WinDef.POINT.ByValue): WinDef.HWND?
}

data class Win32Window(val hwnd: WinDef.HWND) : WindowRef

class WindowsPlatform : Platform {
    val EVENT_SYSTEM_FOREGROUND = 0x0003
    val EVENT_OBJECT_CREATE = 0x8000
    val EVENT_OBJECT_DESTROY = 0x8001
    val WINEVENT_OUTOFCONTEXT = 0x0000
    val WINEVENT_SKIPOWNPROCESS = 0x0002
    val OBJID_WINDOW = 0
    val WS_EX_TOOLWINDOW = 0x00000080L
    val u32i = MyUser32.INSTANCE
    val enum = EnumWindows()
    val cont = WindowController(u32i)
    val hkMg = WindowsHotkeys(u32i)

    private var events: PlatformEvents? = null
    private var loopThreadId = 0

    val proc = object : WinEventProc {
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
                EVENT_OBJECT_DESTROY -> events?.windowClosed(Win32Window(hwnd))
                EVENT_OBJECT_CREATE -> events?.windowsChanged()
                EVENT_SYSTEM_FOREGROUND -> events?.windowsChanged()
            }
        }
    }

    override fun windows(): List<WindowRef> = enum.getWindows().map { Win32Window(it.windowHandle) }

    override fun monitorBounds(window: WindowRef) = cont.getMonitorBounds(window.hwnd)

    override fun setFrame(window: WindowRef, rect: Rect) = cont.apply(window.hwnd, rect)

    override fun hide(window: WindowRef) {
        u32i.ShowWindow(window.hwnd, User32.SW_HIDE)
    }

    override fun show(window: WindowRef) {
        u32i.ShowWindow(window.hwnd, User32.SW_RESTORE)
    }

    override fun focus(window: WindowRef) {
        val hwnd = window.hwnd
        val currentThreadId = WinDef.DWORD(Kernel32.INSTANCE.GetCurrentThreadId().toLong())
        val targetThreadId = WinDef.DWORD(Kernel32.INSTANCE.GetCurrentThreadId().toLong())

        u32i.AttachThreadInput(currentThreadId, targetThreadId, true)

        u32i.ShowWindow(hwnd, User32.SW_RESTORE)
        u32i.SetForegroundWindow(hwnd)
        u32i.SetFocus(hwnd)

        u32i.AttachThreadInput(currentThreadId, targetThreadId, false)
    }

    override fun focusedWindow(): WindowRef? = u32i.GetForegroundWindow()?.let(::Win32Window)

    override fun windowUnderCursor(): WindowRef? {
        val point = WinDef.POINT()
        u32i.GetCursorPos(point)
        val byVal = WinDef.POINT.ByValue()
        byVal.x = point.x
        byVal.y = point.y
        val raw = u32i.WindowFromPoint(byVal) ?: return null
        return u32i.GetAncestor(raw, User32.GA_ROOT)?.let(::Win32Window)
    }

    override fun run(events: PlatformEvents) {
        this.events = events
        loopThreadId = Kernel32.INSTANCE.GetCurrentThreadId()

        // Out-of-context hooks deliver events to the thread that installed them, so install here
        val hHook = u32i.SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_OBJECT_DESTROY, null, proc, 0, 0, WINEVENT_OUTOFCONTEXT or WINEVENT_SKIPOWNPROCESS)
        hkMg.bind()

        val msg = MSG()
        while (u32i.GetMessage(msg, null, 0, 0) > 0) {
            if (msg.message == WM_HOTKEY) {
                hkMg.actionFor(msg.wParam.toInt())?.let(events::hotkey)
            }
        }

        hkMg.unbind()
        u32i.UnhookWinEvent(hHook)
    }

    override fun stop() {
        u32i.PostThreadMessage(loopThreadId, WM_QUIT, null, null)
    }

    private val WindowRef.hwnd get() = (this as Win32Window).hwnd
}
