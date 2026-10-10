package org.example.platform.macos

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer

/** Returning the event passes it on; returning null swallows it. */
interface CGEventTapCallBack : Callback {
    fun invoke(proxy: Pointer?, type: Int, event: Pointer?, userInfo: Pointer?): Pointer?
}

@Suppress("FunctionName")
interface CoreGraphics : Library {
    fun CGWindowListCopyWindowInfo(option: Int, relativeToWindow: Int): Pointer?

    fun CGEventTapCreate(
        tap: Int,
        place: Int,
        options: Int,
        eventsOfInterest: Long,
        callback: CGEventTapCallBack,
        userInfo: Pointer?
    ): Pointer?
    fun CGEventTapEnable(tap: Pointer, enable: Boolean)
    fun CGEventGetIntegerValueField(event: Pointer, field: Int): Long
    fun CGEventGetFlags(event: Pointer): Long

    companion object {
        val INSTANCE: CoreGraphics = Native.load("CoreGraphics", CoreGraphics::class.java)
    }
}
