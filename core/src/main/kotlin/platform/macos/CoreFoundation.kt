package org.example.platform.macos

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer

// C booleans are returned as Byte: JNA would otherwise read a full int and pick up garbage in the upper bits.
@Suppress("FunctionName")
interface CoreFoundation : Library {
    fun CFRetain(ref: Pointer): Pointer
    fun CFRelease(ref: Pointer)
    fun CFEqual(a: Pointer, b: Pointer): Byte
    fun CFHash(ref: Pointer): Long

    fun CFStringCreateWithCString(alloc: Pointer?, cString: String, encoding: Int): Pointer
    fun CFStringGetLength(string: Pointer): Long
    fun CFStringGetMaximumSizeForEncoding(length: Long, encoding: Int): Long
    fun CFStringGetCString(string: Pointer, buffer: ByteArray, bufferSize: Long, encoding: Int): Byte

    fun CFArrayGetCount(array: Pointer): Long
    fun CFArrayGetValueAtIndex(array: Pointer, index: Long): Pointer?

    fun CFDictionaryGetValue(dict: Pointer, key: Pointer): Pointer?
    fun CFDictionaryCreate(
        alloc: Pointer?,
        keys: Array<Pointer>,
        values: Array<Pointer>,
        count: Long,
        keyCallBacks: Pointer,
        valueCallBacks: Pointer
    ): Pointer

    fun CFNumberGetValue(number: Pointer, type: Int, out: Pointer): Byte
    fun CFBooleanGetValue(bool: Pointer): Byte

    fun CFRunLoopGetCurrent(): Pointer
    fun CFRunLoopRun()
    fun CFRunLoopStop(loop: Pointer)
    fun CFRunLoopAddSource(loop: Pointer, source: Pointer, mode: Pointer)
    fun CFMachPortCreateRunLoopSource(alloc: Pointer?, port: Pointer, order: Long): Pointer

    companion object {
        val INSTANCE: CoreFoundation = Native.load("CoreFoundation", CoreFoundation::class.java)
    }
}

/** Kotlin helpers over CoreFoundation types. */
object CF {
    private val lib = CoreFoundation.INSTANCE
    private val globals = NativeLibrary.getInstance("CoreFoundation")

    private const val UTF8 = 0x08000100
    private const val NUMBER_SINT64 = 4

    val TRUE: Pointer = globals.getGlobalVariableAddress("kCFBooleanTrue").getPointer(0)
    val RUN_LOOP_COMMON_MODES: Pointer = globals.getGlobalVariableAddress("kCFRunLoopCommonModes").getPointer(0)
    private val dictKeyCallBacks = globals.getGlobalVariableAddress("kCFTypeDictionaryKeyCallBacks")
    private val dictValueCallBacks = globals.getGlobalVariableAddress("kCFTypeDictionaryValueCallBacks")

    /** Creates a CFString the caller owns. Use for long-lived constants like attribute names. */
    fun string(value: String): Pointer = lib.CFStringCreateWithCString(null, value, UTF8)

    fun toKString(ref: Pointer): String {
        val size = lib.CFStringGetMaximumSizeForEncoding(lib.CFStringGetLength(ref), UTF8) + 1
        val buffer = ByteArray(size.toInt())
        if (lib.CFStringGetCString(ref, buffer, size, UTF8).toInt() == 0) return ""
        return Native.toString(buffer, "UTF-8")
    }

    fun toLong(number: Pointer): Long {
        val out = Memory(8)
        lib.CFNumberGetValue(number, NUMBER_SINT64, out)
        return out.getLong(0)
    }

    fun toBoolean(bool: Pointer): Boolean = lib.CFBooleanGetValue(bool).toInt() != 0

    /** The array keeps ownership of its elements; retain any you keep past the array's lifetime. */
    fun toList(array: Pointer): List<Pointer> =
        (0 until lib.CFArrayGetCount(array)).mapNotNull { lib.CFArrayGetValueAtIndex(array, it) }

    fun dictionary(vararg entries: Pair<Pointer, Pointer>): Pointer = lib.CFDictionaryCreate(
        null,
        entries.map { it.first }.toTypedArray(),
        entries.map { it.second }.toTypedArray(),
        entries.size.toLong(),
        dictKeyCallBacks,
        dictValueCallBacks
    )

    fun get(dict: Pointer, key: Pointer): Pointer? = lib.CFDictionaryGetValue(dict, key)

    fun equal(a: Pointer, b: Pointer) = lib.CFEqual(a, b).toInt() != 0
    fun hash(ref: Pointer) = lib.CFHash(ref)
    fun retain(ref: Pointer): Pointer = lib.CFRetain(ref)
    fun release(ref: Pointer?) {
        if (ref != null) lib.CFRelease(ref)
    }
}
