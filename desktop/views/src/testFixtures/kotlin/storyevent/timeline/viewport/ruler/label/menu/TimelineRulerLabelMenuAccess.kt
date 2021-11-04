package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot

class TimelineRulerLabelMenuAccess private constructor(private val menu: TimelineRulerLabelMenu) : FxRobot() {
    companion object {
        fun TimelineRulerLabelMenu.access(): TimelineRulerLabelMenuAccess = TimelineRulerLabelMenuAccess(this)
        fun TimelineRulerLabelMenu.access(op: TimelineRulerLabelMenuAccess.() -> Unit) = access().op()
        fun <T> TimelineRulerLabelMenu.drive(op: TimelineRulerLabelMenuAccess.() -> T): T {
            val accessor = access()
            var result: Result<T>? = null
            accessor.interact {
                result = kotlin.runCatching { accessor.op() }
            }
            return result!!.getOrThrow()
        }
    }

    val insertBeforeOption: MenuItem?
        get() = menu.items.find { it.id == "insert-before" }

    val insertAfterOption: MenuItem?
        get() = menu.items.find { it.id == "insert-after" }

    val removeTimeOption: MenuItem?
        get() = menu.items.find { it.id == "delete" }



}