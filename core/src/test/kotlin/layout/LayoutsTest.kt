package org.example.layout

import org.example.win.MonitorBounds
import kotlin.test.Test
import kotlin.test.assertTrue

class LayoutsTest {
    // Doesn't start at the origin, like a second monitor or a macOS screen below the menu bar
    private val screen = MonitorBounds(x = 100, y = 33, width = 1728, height = 1084)

    private val all = listOf(
        LeftHalf, RightHalf, TopHalf, BottomHalf, FullScreen,
        RightTopThird, RightMiddleThird, RightBottomThird
    )

    private val Rect.right get() = x + w
    private val Rect.bottom get() = y + h

    @Test
    fun `every layout stays on screen below the bar`() {
        all.forEach { layout ->
            val rect = layout.rect(screen)
            assertTrue(rect.x >= screen.x, "$layout starts left of the screen: $rect")
            assertTrue(rect.y >= screen.y + 40, "$layout covers the bar: $rect")
            assertTrue(rect.right <= screen.x + screen.width, "$layout runs off the right edge: $rect")
            assertTrue(rect.bottom <= screen.y + screen.height, "$layout runs off the bottom edge: $rect")
        }
    }

    @Test
    fun `stacked windows do not overlap`() {
        val stacks = listOf(
            listOf(TopHalf, BottomHalf),
            listOf(RightTopThird, RightMiddleThird, RightBottomThird)
        )
        stacks.forEach { stack ->
            stack.zipWithNext().forEach { (upper, lower) ->
                val above = upper.rect(screen)
                val below = lower.rect(screen)
                assertTrue(above.bottom <= below.y, "$upper overlaps $lower: $above / $below")
            }
        }
    }

    @Test
    fun `halves split the screen without overlapping`() {
        assertTrue(LeftHalf.rect(screen).right <= RightHalf.rect(screen).x)
    }
}
