package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.di.DI
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.list.StoryEventListToolView
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventDetails
import javafx.scene.control.RadioButton
import javafx.scene.control.Tab
import org.junit.jupiter.api.Assertions.*
import tornadofx.select
import tornadofx.selectedItem

fun WorkBench.getOpenStoryEventListTool(): StoryEventListToolView? {
    val components = DI.getRegisteredTypes(scope)
    val instance = components[StoryEventListToolView::class]
    val view = instance as? StoryEventListToolView
    return view?.takeIf { it.scene?.window?.isShowing == true }
}

fun WorkBench.getOpenStoryEventListToolOrError(): StoryEventListToolView = getOpenStoryEventListTool()
    ?: error("Story Event List Tool was not opened in this workbench ${scope.projectViewModel}")

fun WorkBench.givenStoryEventListToolHasBeenOpened(): StoryEventListToolView = getOpenStoryEventListTool() ?: run {
    openStoryEventListTool()
    getOpenStoryEventListToolOrError()
}

fun WorkBench.openStoryEventListTool() {
    val view = DI.getRegisteredTypes(scope)[StoryEventListToolView::class] as? StoryEventListToolView
    if (view != null) {
        println("selecting tab...")
        robot.interact {
            val tab = view.properties["tornadofx.tab"] as Tab
            tab.select()
            assertTrue(tab.isSelected)
            assertNotNull(tab.tabPane.scene)
            assertNotNull(view.scene)
            assertNotNull(view.scene.window)
            assertTrue(view.scene.window.isShowing)
        }
    } else {
        findMenuItemById("tools_storyeventlist")!!
            .apply { robot.interact { fire() } }
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
    val storyEventIdSet = storyEvents.map { it.id }.toSet()
    if (access().storyEventList?.selectionModel?.selectedItems?.map { it.id }?.toSet() == storyEventIdSet) return this
    drive {
        storyEventList!!.selectionModel!!.clearSelection()
        storyEvents.forEach { storyEvent ->
            storyEventList!!.selectionModel!!.select(storyEventItems.find { it.id == storyEvent.id })
        }
    }
    assertEquals(
        storyEventIdSet,
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

fun StoryEventListToolView.openStoryEventDetailsFor(storyEventId: StoryEvent.Id) {
    drive {
        storyEventList!!.selectionModel.clearSelection()
        storyEventList!!.selectionModel.select(storyEventList!!.items.find { it.id == storyEventId })
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

fun StoryEventListToolView.coverSelectedStoryEventIn(sceneId: Scene.Id) {
    val coverageMenu = drive {
        optionsButton!!.show()
        optionsButton!!.coverageMenu!!.apply { show() }
    }
    awaitWithTimeout(100) {
        with(access()) {
            coverageMenu.options.first().id != "loading"
        }
    }
    drive {
        coverageMenu.run {
            (options.find { it.id == sceneId.toString() } as RadioButton).fire()
        }
    }
}

fun StoryEventListToolView.uncoverSelectedStoryEvent() {
    val coverageMenu = drive {
        optionsButton!!.show()
        optionsButton!!.coverageMenu!!.apply { show() }
    }
    awaitWithTimeout(100) {
        with(access()) {
            coverageMenu.options.first().id != "loading"
        }
    }
    drive {
        coverageMenu.run {
            (options.find { it is RadioButton && it.isSelected } as RadioButton).fire()
        }
    }
}