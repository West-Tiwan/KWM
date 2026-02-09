package org.example.layout

import org.example.win.MonitorBounds

interface Layout {
    fun rect(monitorBounds: MonitorBounds): Rect
}