package org.example.platform.macos

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference

@Suppress("FunctionName")
interface ApplicationServices : Library {
    fun AXIsProcessTrusted(): Byte
    fun AXIsProcessTrustedWithOptions(options: Pointer?): Byte
    fun AXUIElementCreateSystemWide(): Pointer
    fun AXUIElementCreateApplication(pid: Int): Pointer
    fun AXUIElementCopyAttributeValue(element: Pointer, attribute: Pointer, value: PointerByReference): Int
    fun AXUIElementSetAttributeValue(element: Pointer, attribute: Pointer, value: Pointer): Int
    fun AXUIElementPerformAction(element: Pointer, action: Pointer): Int
    fun AXUIElementCopyElementAtPosition(application: Pointer, x: Float, y: Float, element: PointerByReference): Int
    fun AXUIElementGetPid(element: Pointer, pid: IntByReference): Int
    fun AXUIElementSetMessagingTimeout(element: Pointer, timeoutInSeconds: Float): Int
    fun AXValueCreate(type: Int, value: Pointer): Pointer?
    fun AXValueGetValue(value: Pointer, type: Int, out: Pointer): Byte

    companion object {
        val INSTANCE: ApplicationServices = Native.load("ApplicationServices", ApplicationServices::class.java)
    }
}

/** Kotlin helpers over the macOS Accessibility (AX) API. Functions named copy* return objects the caller must release. */
object AX {
    private val lib = ApplicationServices.INSTANCE

    private const val SUCCESS = 0
    private const val VALUE_CG_POINT = 1
    private const val VALUE_CG_SIZE = 2

    val WINDOWS = CF.string("AXWindows")
    val WINDOW = CF.string("AXWindow")
    val ROLE = CF.string("AXRole")
    val SUBROLE = CF.string("AXSubrole")
    val MINIMIZED = CF.string("AXMinimized")
    val FULL_SCREEN = CF.string("AXFullScreen")
    val POSITION = CF.string("AXPosition")
    val SIZE = CF.string("AXSize")
    val MAIN = CF.string("AXMain")
    val FRONTMOST = CF.string("AXFrontmost")
    val FOCUSED_WINDOW = CF.string("AXFocusedWindow")
    val RAISE = CF.string("AXRaise")

    val systemWide: Pointer by lazy { lib.AXUIElementCreateSystemWide() }

    fun isTrusted() = lib.AXIsProcessTrusted().toInt() != 0

    /** Asks macOS to show the "allow accessibility" prompt for this process if it isn't trusted yet. */
    fun promptForTrust(): Boolean {
        val promptKey = NativeLibrary.getInstance("ApplicationServices")
            .getGlobalVariableAddress("kAXTrustedCheckOptionPrompt")
            .getPointer(0)
        val options = CF.dictionary(promptKey to CF.TRUE)
        try {
            return lib.AXIsProcessTrustedWithOptions(options).toInt() != 0
        } finally {
            CF.release(options)
        }
    }

    /** Stops one unresponsive app from stalling every AX call. */
    fun setTimeout(seconds: Float) {
        lib.AXUIElementSetMessagingTimeout(systemWide, seconds)
    }

    fun copyApplication(pid: Int): Pointer = lib.AXUIElementCreateApplication(pid)

    fun copy(element: Pointer, attribute: Pointer): Pointer? {
        val out = PointerByReference()
        if (lib.AXUIElementCopyAttributeValue(element, attribute, out) != SUCCESS) return null
        return out.value
    }

    fun string(element: Pointer, attribute: Pointer): String? = withCopy(element, attribute) { CF.toKString(it) }

    fun bool(element: Pointer, attribute: Pointer): Boolean? = withCopy(element, attribute) { CF.toBoolean(it) }

    fun position(element: Pointer): Pair<Double, Double>? = withCopy(element, POSITION) { readPair(it, VALUE_CG_POINT) }

    fun size(element: Pointer): Pair<Double, Double>? = withCopy(element, SIZE) { readPair(it, VALUE_CG_SIZE) }

    fun setPosition(element: Pointer, x: Double, y: Double) = setPair(element, POSITION, VALUE_CG_POINT, x, y)

    fun setSize(element: Pointer, width: Double, height: Double) = setPair(element, SIZE, VALUE_CG_SIZE, width, height)

    fun setTrue(element: Pointer, attribute: Pointer) {
        lib.AXUIElementSetAttributeValue(element, attribute, CF.TRUE)
    }

    fun perform(element: Pointer, action: Pointer) {
        lib.AXUIElementPerformAction(element, action)
    }

    fun pid(element: Pointer): Int? {
        val out = IntByReference()
        return if (lib.AXUIElementGetPid(element, out) == SUCCESS) out.value else null
    }

    /** Hit-tests inside one app. Never pass KWM's own app: see [MacWindows.appAt]. */
    fun copyElementAt(app: Pointer, x: Double, y: Double): Pointer? {
        val out = PointerByReference()
        if (lib.AXUIElementCopyElementAtPosition(app, x.toFloat(), y.toFloat(), out) != SUCCESS) return null
        return out.value
    }

    private inline fun <T> withCopy(element: Pointer, attribute: Pointer, read: (Pointer) -> T?): T? {
        val value = copy(element, attribute) ?: return null
        try {
            return read(value)
        } finally {
            CF.release(value)
        }
    }

    // CGPoint and CGSize are both two CGFloats (doubles on 64-bit)
    private fun readPair(value: Pointer, type: Int): Pair<Double, Double>? {
        val out = Memory(16)
        if (lib.AXValueGetValue(value, type, out).toInt() == 0) return null
        return out.getDouble(0) to out.getDouble(8)
    }

    private fun setPair(element: Pointer, attribute: Pointer, type: Int, first: Double, second: Double) {
        val raw = Memory(16)
        raw.setDouble(0, first)
        raw.setDouble(8, second)
        val value = lib.AXValueCreate(type, raw) ?: return
        try {
            lib.AXUIElementSetAttributeValue(element, attribute, value)
        } finally {
            CF.release(value)
        }
    }
}

object MacPermissions {
    /** Blocks until the user grants Accessibility access, which macOS requires to move other apps' windows. */
    fun awaitAccessibility() {
        if (AX.isTrusted()) return
        AX.promptForTrust()
        println("KWM needs Accessibility access: System Settings > Privacy & Security > Accessibility")
        while (!AX.isTrusted()) {
            Thread.sleep(1000)
        }
        println("Accessibility access granted")
    }
}
