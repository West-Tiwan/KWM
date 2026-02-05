package org.example

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser.*
import org.example.hotkey.HotkeyManager
import org.example.win.WindowManager

fun main() {
    val u32i = User32.INSTANCE
    val EVENT_SYSTEM_FOREGROUND = 0x0003
    val EVENT_OBJECT_CREATE = 0x8000
    val EVENT_OBJECT_DESTROY = 0x8001
    val WINEVENT_OUTOFCONTEXT = 0x0000
    val WINEVENT_SKIPOWNPROCESS = 0x0002
    val OBJID_WINDOW = 0

    val hkMg = HotkeyManager(u32i)
    val winMan = WindowManager()

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
            if (idObject?.toInt() != OBJID_WINDOW) return
            when (event?.toInt()) {
                EVENT_SYSTEM_FOREGROUND,
                EVENT_OBJECT_CREATE,
                EVENT_OBJECT_DESTROY -> winMan.retile()
            }
        }
    }
    val hHook = u32i.SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_OBJECT_DESTROY, null,
        proc, 0, 0,
        WINEVENT_OUTOFCONTEXT or WINEVENT_SKIPOWNPROCESS)
    if (hHook == null) {
        println("failed to install hook")
    }
    println("hook listening")

    hkMg.bind()
    val msg = MSG()

    while (u32i.GetMessage(msg, null, 0, 0) > 0) {
        if (msg.message == WM_HOTKEY) {
            winMan.retile()
        }
    }
    u32i.UnhookWinEvent(hHook)
}