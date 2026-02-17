package org.example.layout

import org.example.win.MonitorBounds

object LeftHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x, monitorBounds.y + 40, monitorBounds.width/2, monitorBounds.height - 40)
}
object RightHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, monitorBounds.y + 40, monitorBounds.width/2, monitorBounds.height - 40)
}
object TopHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, monitorBounds.y + 40, monitorBounds.width/2, monitorBounds.height/2 - 20)
}
object BottomHalf: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, monitorBounds.height/2 + 20, monitorBounds.width/2, monitorBounds.height/2 - 20)
}
object FullScreen: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x, monitorBounds.y + 40, monitorBounds.width, monitorBounds.height - 40)
}
object RightTopThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, monitorBounds.y + 40, monitorBounds.width/2, monitorBounds.height/3 - 14)
}
object RightMiddleThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, monitorBounds.height/3 + 26, monitorBounds.width/2, monitorBounds.height/3 - 12)
}
object RightBottomThird: Layout {
    override fun rect(monitorBounds: MonitorBounds) = Rect(monitorBounds.x + monitorBounds.width/2, 2*monitorBounds.height/3 + 12, monitorBounds.width/2, monitorBounds.height/3 - 12)
}