package org.example.platform

enum class OperatingSystem {
    WINDOWS,
    MACOS;

    companion object {
        val current: OperatingSystem by lazy {
            val name = System.getProperty("os.name").lowercase()
            when {
                name.startsWith("windows") -> WINDOWS
                name.startsWith("mac") -> MACOS
                else -> throw UnsupportedOperationException("KWM does not support $name")
            }
        }
    }
}
