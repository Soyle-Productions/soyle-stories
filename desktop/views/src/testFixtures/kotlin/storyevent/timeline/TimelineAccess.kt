package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.timeline.Timeline
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.timelineHeaderArea
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import tornadofx.Stylesheet
import tornadofx.hasClass

class TimelineAccess private constructor(val timeline: Timeline) : NodeAccess<Timeline>(timeline) {
    companion object {
        fun Timeline.access() = TimelineAccess(this)
        fun <T> Timeline.drive(op: TimelineAccess.() -> T): T
        {
            var result: Result<T> = Result.failure(NotImplementedError())
            val access = access()
            access.interact { result = Result.success(access.op()) }
            return result.getOrThrow()
        }
    }

    private val header: HBox? by temporaryChild(timelineHeaderArea)

    val createButton: Button?
        get() = header.findChild(Stylesheet.button) { it.hasClass(primary) }

    val optionsButton: MenuButton?
        get() = header.findChild(Stylesheet.menuButton)

    val MenuButton.deleteStoryEventOption: MenuItem?
        get() = items.find { it.id == "delete" }

    val MenuButton.insertTimeBeforeOption: MenuItem?
        get() = items.find { it.id == "insert-before" }

    val MenuButton.insertTimeAfterOption: MenuItem?
        get() = items.find { it.id == "insert-after" }

    val condensedToggle: CheckBox?
        get() = header.findChild(Stylesheet.checkBox)

    val viewport: TimelineViewPort? by temporaryChild(Stylesheet.viewport)

    fun timeInView(unit: Long): Boolean
    {
        val viewport = viewport ?: return false
        val requiredPixels = viewport.scale(UnitOfTime(unit))
        return requiredPixels.value in viewport.offsetX.value .. viewport.offsetX.value+viewport.width
    }
}