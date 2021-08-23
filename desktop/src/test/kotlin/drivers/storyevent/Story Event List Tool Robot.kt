package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.di.get
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneList.SceneListView
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.fail
import tornadofx.FX
import tornadofx.selectedItem

fun WorkBench.getOpenStoryEventListTool(): StoryEventListTool? =
    scope.get<StoryEventListTool>().takeIf { it.root.scene?.window?.isShowing == true }

fun WorkBench.getOpenStoryEventListToolOrError(): StoryEventListTool = getOpenStoryEventListTool()
    ?: error("Story Event List Tool was not opened in this workbench ${scope.projectViewModel}")

fun WorkBench.givenStoryEventListToolHasBeenOpened(): StoryEventListTool = getOpenStoryEventListTool() ?: run {
    openStoryEventListTool()
    getOpenStoryEventListToolOrError()
}

fun WorkBench.openStoryEventListTool() {
    findMenuItemById("tools_storyeventlist")!!
        .apply { robot.interact { fire() } }
}

fun StoryEventListTool.openCreateStoryEventDialog() {
    drive {
        createStoryEventButton!!.fire()
    }
}

/**
 * @param placement either "before", "after", or "at the same time as"
 */
fun StoryEventListTool.openCreateRelativeStoryEventDialog(placement: String) {
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

fun StoryEventListTool.givenStoryEventHasBeenSelected(storyEvent: StoryEvent): StoryEventListTool {
    if (access().storyEventList?.selectedItem?.id == storyEvent.id.uuid.toString()) return this
    drive {
        storyEventList!!.selectionModel!!.select(storyEventItems.find { it.id == storyEvent.id.uuid.toString() })
    }
    if (access().storyEventList?.selectedItem?.id != storyEvent.id.uuid.toString()) {
        fail<Nothing>("Did not correctly select the story event")
    }
    return this
}