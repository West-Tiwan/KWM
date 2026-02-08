package org.example.layout

import org.example.win.MonitorBounds

object LeftHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x, monitorBounds.y, monitorBounds.width/2, monitorBounds.height)
}
object RightHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, monitorBounds.y, monitorBounds.width/2, monitorBounds.height)
}
object TopHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, monitorBounds.y, monitorBounds.width/2, monitorBounds.height/2)
}
object BottomHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, monitorBounds.height/2, monitorBounds.width/2, monitorBounds.height/2)
}
object FullScreen: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x, monitorBounds.y, monitorBounds.width, monitorBounds.height)
}
object RightTopThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, monitorBounds.y, monitorBounds.width/2, monitorBounds.height/3)
}
object RightMiddleThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, monitorBounds.height/3, monitorBounds.width/2, monitorBounds.height/3)
}
object RightBottomThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.width/2, 2*monitorBounds.height/3, monitorBounds.width/2, monitorBounds.height/3)
}