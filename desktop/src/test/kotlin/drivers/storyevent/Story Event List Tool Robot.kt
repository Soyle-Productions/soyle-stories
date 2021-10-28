package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.layout.config.fixed.StoryEventList
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.list.StoryEventListTool
import com.soyle.stories.storyevent.list.StoryEventListToolView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import tornadofx.lookup
import tornadofx.selectedItem

fun WorkBench.getOpenStoryEventListTool(): StoryEventListToolView? =
    (DI.getRegisteredTypes(scope)[StoryEventListToolView::class] as? StoryEventListToolView)
        ?.takeIf { it.scene?.window?.isShowing == true }

fun WorkBench.getOpenStoryEventListToolOrError(): StoryEventListToolView = getOpenStoryEventListTool()
    ?: error("Story Event List Tool was not opened in this workbench ${scope.projectViewModel}")

fun WorkBench.givenStoryEventListToolHasBeenOpened(): StoryEventListToolView = getOpenStoryEventListTool() ?: run {
    openStoryEventListTool()
    getOpenStoryEventListToolOrError()
}

fun WorkBench.openStoryEventListTool() {
    findMenuItemById("tools_storyeventlist")!!
        .apply {
            robot.interact {
                fire()
            }
        }
}

fun StoryEventListToolView.openCreateStoryEventDialog() {
    drive {
        createStoryEventButton!!.fire()
    }
}

fun StoryEventListToolView.givenStoryEventHasBeenSelected(storyEvent: StoryEvent): StoryEventListToolView {
    if (access().storyEventList?.selectedItem?.id == storyEvent.id) return this
    drive {
        storyEventList!!.selectionModel!!.select(storyEventItems.find { it.id == storyEvent.id })
    }
    if (access().storyEventList?.selectedItem?.id != storyEvent.id) {
        fail<Nothing>("Did not correctly select the story event")
    }
    return this
}

fun StoryEventListToolView.givenStoryEventsHaveBeenSelected(storyEvents: List<StoryEvent>): StoryEventListToolView {
    if (access().storyEventList?.selectionModel?.selectedItems?.map { it.id }?.toSet() == storyEvents.map { it.id }
            .toSet()) return this
    drive {
        storyEvents.forEach { storyEvent ->
            storyEventList!!.selectionModel!!.select(storyEventItems.find { it.id == storyEvent.id })
        }
    }
    assertEquals(
        storyEvents.map { it.id }.toSet(),
        access().storyEventList!!.selectionModel.selectedItems.map { it.id }.toSet()
    ) {
        "Did not correctly select all of the story events"
    }
    return this
}

/**
 * @param placement either "before", "after", or "at the same time as"
 */
fun StoryEventListToolView.openCreateRelativeStoryEventDialog(placement: String) {
    if (placement !in setOf(
            "before",
            "after",
            "at the same time as"
        )
    ) fail<Nothing>("Placement should be either \"before\", \"after\", or \"at the same time as\".  Received: \"$placement\"")
    drive {
        optionsButton!!.show()
        val option = optionsButton!!.insertNewStoryEventOption(placement.replace(" ", "-"))
            ?: fail("No option to insert new story event $placement")
        option.fire()
    }
}

fun StoryEventListToolView.openRenameStoryEventDialog() {
    drive {
        optionsButton!!.show()
        optionsButton!!.renameOption!!.fire()
    }
}

fun StoryEventListToolView.openRescheduleStoryEventDialog() {
    drive {
        optionsButton!!.show()
        optionsButton!!.rescheduleOption!!.fire()
    }
}

fun StoryEventListToolView.openStoryEventTimeAdjustmentDialog() {
    drive {
        optionsButton!!.show()
        optionsButton!!.adjustTimeOption!!.fire()
    }
}

fun StoryEventListToolView.openDeleteStoryEventDialog() {
    drive {
        optionsButton!!.show()
        optionsButton!!.deleteOption!!.fire()
    }
}

fun StoryEventListToolView.viewStoryEventInTimeline() {
    drive {
        optionsButton!!.show()
        optionsButton!!.viewInTimeline!!.fire()
    }
}