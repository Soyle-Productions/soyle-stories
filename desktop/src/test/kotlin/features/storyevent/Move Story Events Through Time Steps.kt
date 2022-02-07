package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.testfx.assertions.api.Assertions

class `Move Story Events Through Time Steps` : StoryEventFeatureSteps {

    init {
        Given("I have selected the following story events to increment their time") { data: DataTable ->
            val selectedStoryEvents = data.asList().map { name ->
                storyEvents.givenStoryEventExists(NonBlankString.create(name)!!, 0)
            }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventsHaveBeenSelected(selectedStoryEvents)
        }


        When("I move the {story event} to time {int}") { storyEvent: StoryEvent, time: Int ->
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
        When("I reschedule the {story event} to time {int}") { storyEvent: StoryEvent, time: Int ->
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


        Then(
            "the {story event} should (still )happen at time {int}"
        ) { storyEvent: StoryEvent, expectedTime: Int ->
            Assertions.assertThat(storyEvent.time.toInt()).isEqualTo(expectedTime)

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .assertThis {
                    hasStoryEvent(storyEvent)
                }
        }
        Then("the following story events should take place at these times") { data: DataTable ->
            data.asMaps().forEach {
                val expectedTime = it.getValue("Time").toLong()
                val storyEvent = storyEvents.getStoryEventByName(it.getValue("Name"))!!
                org.junit.jupiter.api.Assertions.assertEquals(expectedTime, storyEvent.time.toLong()) {
                    "${storyEvent.name} should take place at time $expectedTime, but was found at time ${storyEvent.time}"
                }
            }
        }
    }

}