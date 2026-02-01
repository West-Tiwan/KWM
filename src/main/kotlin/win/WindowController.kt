package org.example.win

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser

class WindowController (val u32i: User32) {

    fun snapLeft(hwnd: WinDef.HWND,h: Int,w: Int) {
        u32i.SetWindowPos(
            hwnd,
            null,
            0,
            0,
            w/2,
            h,
            WinUser.SWP_NOZORDER
        )
    }
    fun snapRight(hwnd: WinDef.HWND,h: Int,w: Int) {
        u32i.SetWindowPos(
            hwnd,
            null,
            w/2,
            0,
            w/2,
            h,
            WinUser.SWP_NOZORDER
        )
    }
    fun snapTop(hwnd: WinDef.HWND,h: Int,w: Int) {
        u32i.SetWindowPos(
            hwnd,
            null,
            0,
            0,
            w,
            h/2,
            WinUser.SWP_NOZORDER
        )
    }
    fun snapBottom(hwnd: WinDef.HWND,h: Int,w: Int) {
        u32i.SetWindowPos(
            hwnd,
            null,
            0,
            h/2,
            w,
            h/2,
            WinUser.SWP_NOZORDER
        )
    }
}