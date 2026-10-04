package org.example.platform.macos

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer

@Suppress("FunctionName")
interface CoreGraphics : Library {
    fun CGWindowListCopyWindowInfo(option: Int, relativeToWindow: Int): Pointer?

    companion object {
        val INSTANCE: CoreGraphics = Native.load("CoreGraphics", CoreGraphics::class.java)
    }
}
