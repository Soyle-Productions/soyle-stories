package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAssertions.Companion.assertThat
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.CheckMenuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.fail
import org.testfx.assertions.api.Assertions.assertThat

class `Story Event Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    val storyEvents: `Story Event Robot`
        get() = `Story Event Robot`(soyleStories.getAnyOpenWorkbenchOrError())

    private fun givens() {
        Given("I have created and covered the following story events in the {scene}") { scene: Scene, data: DataTable ->
            val storyEventRobot = storyEvents
            data.asList().forEach { name ->
                val storyEvent = storyEventRobot.givenStoryEventExists(NonBlankString.create(name)!!)
                storyEventRobot.givenStoryEventIsCoveredByScene(storyEvent, scene.id)
            }
        }
        Given("I am renaming the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenRenameStoryEventDialogHasBeenOpened(storyEvent)
        }
        Given("I am viewing the story event timeline for my story") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenTimelineToolHasBeenOpened()
        }
    }

    private fun whens() {
        When("I rename the {story event} to {string}") { storyEvent: StoryEvent, newName: String ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            workBench
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .openRenameStoryEventDialog()

            workBench
                .getOpenRenameStoryEventDialogOrError()
                .renameStoryEvent(newName)
            runBlocking { delay(100) } // given events time to propagate
        }
        When("I uncover the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .uncoverSelectedStoryEvent()
        }
        When("I view the story event timeline for my story") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .openTimelineTool()
        }
        When("I view the {story event} in the timeline for my story") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .viewStoryEventInTimeline()
        }
        When("I insert {int} units of time before time unit {int}") { insertAmount: Int, unit: Int ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenTimelineToolHasBeenOpened()
                .givenTimeUnitInView(unit.toLong())
                .givenTimeUnitHasBeenSelected(unit.toLong())
                .openInsertTimeDialog(before = true)

            workbench.getOpenInsertTimeDialogOrError()
                .insertTime(insertAmount.toLong())
        }
    }

    private fun thens() {
        Then("I should still be renaming the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRenameStoryEventDialogOrError()
        }
        Then("I should see an error that I cannot rename a story event to have a blank name") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRenameStoryEventDialogOrError()
                .access()
                .error!!
        }
        Then(
            "the story event originally named {string} should be named {string}"
        ) { oldName: String, newName: String ->
            val storyEvent = storyEvents.getStoryEventByName(newName)!!
            val original = storyEvents.getStoryEventByOldName(oldName)!!
            assertEquals(storyEvent.id, original.id)
        }
        Then("there should not be any story events shown in the timeline for my story") {
            val timeline = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenTimelineToolOrError()
            assertThat(timeline) {
                hasNotStoryEvents()
            }
        }
        Then("I should see the {story event} in the timeline for my story") { storyEvent: StoryEvent ->
            val timeline = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenTimelineToolOrError()

            assertThat(timeline) {
                hasStoryPointLabel(storyEvent.id)
                andStoryPointLabel(storyEvent.id) {
                    hasName(storyEvent.name.value)
                    isAtTime(storyEvent.time.toLong())
                    isInView()
                }
            }
        }
        Then("the {story event} should be focused in the timeline for my story") { storyEvent: StoryEvent ->
            val timeline = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenTimelineToolOrError()
            assertThat(timeline) {
                andStoryPointLabel(storyEvent.id) {
                    isEmphasized()
                }
            }
        }
        Then("I should not see a story event named {string} in the timeline for my story") { name: String ->
            val timeline = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenTimelineToolOrError()
            assertThat(timeline) {
                doesNotHaveStoryPointLabelWithName(name)
            }
        }
        Then(
            "I should see the {story event} at time {int} in the timeline for my story"
        ) { storyEvent: StoryEvent, expectedTime: Int ->
            val timeline = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenTimelineToolOrError()

            assertThat(timeline) {
                hasStoryPointLabel(storyEvent.id)
                andStoryPointLabel(storyEvent.id) {
                    isAtTime(expectedTime.toLong())
                }
            }
        }
        Then("the {story event} should not be covered by a scene") { storyEvent: StoryEvent ->
            assertNull(storyEvent.sceneId)

            val storyEventList = soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)

            with(storyEventList.access()) {
                optionsButton!!.coverageMenu!!.items.none { it is CheckMenuItem && it.isSelected }
            }.let(::assertTrue)
        }
    }

}