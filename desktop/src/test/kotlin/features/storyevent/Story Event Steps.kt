package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
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
        Given("I have created a story event named {string} at time {int}") { name: String, time: Int ->
            storyEvents.givenStoryEventExists(withName = NonBlankString.create(name)!!, atTime = time)
        }
        Given("I have created the following story events") { data: DataTable ->
            data.asLists().drop(1).forEach { (name, time) ->
                storyEvents.givenStoryEventExists(withName = NonBlankString.create(name)!!, atTime = time.toInt())
            }
        }
        Given("I have selected the following story events to increment their time") { data: DataTable ->
            val selectedStoryEvents = data.asList().map { name ->
                storyEvents.givenStoryEventExists(NonBlankString.create(name)!!, 0)
            }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventsHaveBeenSelected(selectedStoryEvents)
        }
        Given("I am deleting the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenDeleteStoryEventDialogHasBeenOpened(listOf(storyEvent))
        }
    }

    private fun whens() {
        When("I create a story event named {string}") { name: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateStoryEventDialogHasBeenOpened()
                .createStoryEventNamed(name)
        }
        When("I create a story event named {string} at time {int}") { name: String, time: Int ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateStoryEventDialogHasBeenOpened()
                .createStoryEventNamed(name, time)
            runBlocking { delay(100) } // given events time to propagate
        }
        When(
            "I create a story event named {string} {} the {story event}"
        ) { name: String, placement: String, relativeStoryEvent: StoryEvent ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            workBench
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(relativeStoryEvent)
                .openCreateRelativeStoryEventDialog(placement = placement)

            workBench
                .getOpenCreateStoryEventDialogOrError()
                .createStoryEventNamed(name)
            runBlocking { delay(100) } // given events time to propagate
        }
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
        When("I change the {story event}'s time to {int}") { storyEvent: StoryEvent, time: Int ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            workBench
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .openRescheduleStoryEventDialog()

            workBench
                .getOpenStoryEventTimeAdjustmentDialogOrError()
                .reschedule(time.toLong())
            runBlocking { delay(100) } // given events time to propagate
        }
        When("I increment the selected story events' times by {int}") { adjustment: Int ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            workBench
                .givenStoryEventListToolHasBeenOpened()
                .openStoryEventTimeAdjustmentDialog()

            workBench
                .getOpenStoryEventTimeAdjustmentDialogOrError()
                .adjustTime(by = adjustment.toLong())
            runBlocking { delay(100) } // given events time to propagate
        }
        When("I want to delete the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .openDeleteStoryEventDialog()
        }
        When("I confirm I want to delete the {story event}") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteStoryEventDialogOrError()
                .confirm()
            runBlocking { delay(100) } // given events time to propagate
        }
    }

    private fun thens() {
        Then("a story event named {string} should have been created") { expectedName: String ->
            val storyEvent = storyEvents.getStoryEventByName(expectedName)
                ?: fail("Story event named \"$expectedName\" was not created")

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .assertThis {
                    hasStoryEvent(storyEvent)
                }
        }
        Then("the {story event} should be at time {int}") { storyEvent: StoryEvent, expectedTime: Int ->
            assertThat(storyEvent.time.toInt()).isEqualTo(expectedTime)

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .assertThis {
                    hasStoryEvent(storyEvent)
                }
        }
        Then("there should not be a story event named {string}") { nameThatShouldNotExist: String ->
            assertNull(storyEvents.getStoryEventByName(nameThatShouldNotExist))

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .assertThis {
                    doesNotHaveStoryEventNamed(nameThatShouldNotExist)
                }
        }
        Then("the following story events should take place at these times") { data: DataTable ->
            data.asMaps().forEach {
                val expectedTime = it.getValue("Time").toLong()
                val storyEvent = storyEvents.getStoryEventByName(it.getValue("Name"))!!
                assertEquals(expectedTime, storyEvent.time) {
                    "${storyEvent.name} should take place at time $expectedTime, but was found at time ${storyEvent.time}"
                }
            }
        }
        Then("I should be prompted to confirm deleting the {story event}") { storyEvent: StoryEvent ->
            val dialog = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteStoryEventDialog()
            assertNotNull(dialog)
        }
        Then("the {story event} should not have been deleted") { storyEvent: StoryEvent ->
            // if we make it here, it should pass since the storyEvent was found
        }
    }

}