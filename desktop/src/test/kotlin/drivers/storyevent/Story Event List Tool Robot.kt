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
import org.junit.jupiter.api.Assertions.fail
import tornadofx.lookup
import tornadofx.selectedItem

fun WorkBench.getOpenStoryEventListTool(): StoryEventListToolView? =
    (DI.getRegisteredTypes(scope)[StoryEventListToolView::class] as? StoryEventListToolView)
        ?.takeIf { it.root.scene?.window?.isShowing == true }

fun WorkBench.getOpenStoryEventListToolOrError(): StoryEventListToolView = getOpenStoryEventListTool()
    ?: error("Story Event List Tool was not opened in this workbench ${scope.projectViewModel}")

fun WorkBench.givenStoryEventListToolHasBeenOpened(): StoryEventListToolView = getOpenStoryEventListTool() ?: run {
    openStoryEventListTool()
    getOpenStoryEventListToolOrError()
}

fun WorkBench.openStoryEventListTool() {
    findMenuItemById("tools_storyeventlist")!!
        .apply { robot.interact { fire() } }
}

fun StoryEventListToolView.openCreateStoryEventDialog() {
    drive {
        createStoryEventButton!!.fire()
    }
}

fun StoryEventListToolView.givenStoryEventHasBeenSelected(storyEvent: StoryEvent): StoryEventListToolView {
    if (access().storyEventList?.selectedItem?.id == storyEvent.id.uuid.toString()) return this
    drive {
        storyEventList!!.selectionModel!!.select(storyEventItems.find { it.id == storyEvent.id.uuid.toString() })
    }
    if (access().storyEventList?.selectedItem?.id != storyEvent.id.uuid.toString()) {
        fail<Nothing>("Did not correctly select the story event")
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