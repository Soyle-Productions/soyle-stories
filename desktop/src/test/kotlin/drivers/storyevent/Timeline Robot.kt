package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAccess.Companion.drive
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewportAccess.Companion.access
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.timeline.Timeline
import com.soyle.stories.storyevent.timeline.TimelineStyles
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.unit
import org.junit.jupiter.api.Assertions.assertTrue

fun WorkBench.givenTimelineToolHasBeenOpened(): Timeline =
    getOpenTimelineTool() ?: run {
        openTimelineTool()
        getOpenTimelineToolOrError()
    }

fun WorkBench.getOpenTimelineTool(): Timeline? =
    robot.listWindows().asSequence()
        .mapNotNull { robot.from(it.scene.root).lookup(".${TimelineStyles.timeline.name}").queryAll<Timeline>().firstOrNull() }
        .firstOrNull()

fun WorkBench.getOpenTimelineToolOrError(): Timeline = getOpenTimelineTool()
    ?: error("Timeline tool is not open in $this")

fun WorkBench.openTimelineTool() {
    findMenuItemById("tools_timeline")!!
        .apply {
            robot.interact {
                fire()
            }
        }
}

fun Timeline.givenTimeUnitInView(unit: Long): Timeline {
    drive {
        if (! timeInView(unit)) scrollToTime(unit)
    }
    assertTrue(access().timeInView(unit))
    return this
}

fun Timeline.givenTimeUnitHasBeenSelected(unit: Long): Timeline {
    if (access().viewport!!.selection.timeRange.value?.range?.contains(unit) != true) selectTimeUnit(unit)
    assertTrue(access().viewport!!.selection.timeRange.value?.range?.contains(unit) == true)
    return this
}

fun Timeline.scrollToTime(unit: Long) {
    drive {
        val viewport = viewport!!
        viewport.scrollToTime(UnitOfTime(unit))
    }
}

fun Timeline.openInsertTimeDialog(before: Boolean = false, after: Boolean = false) {
    assert(before || after && (before != after)) { "Only one option may be selected" }
    drive {
        optionsButton!!.show()
        when {
            before -> optionsButton!!.insertTimeBeforeOption!!.fire()
            after -> optionsButton!!.insertTimeAfterOption!!.fire()
        }
    }
}

fun Timeline.selectTimeUnit(unit: Long) {
    drive {
        val label = viewport!!.access().ruler.labels().value.single { it.range.hasOverlapWith(unit.unit .. unit.unit) }
        clickOn(label)
    }
}