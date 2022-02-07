package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.confirm
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.showRamifications
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenRamificationsToolOrError
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenReportOrError
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.Labeled
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import tornadofx.uiComponent

class `Remove Story Event from Story Steps` : StoryEventFeatureSteps {

    init {
        Given("I am removing the {story event} from the story") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenDeleteStoryEventDialogHasBeenOpened(listOf(storyEvent))
        }
        Given("I have removed the {story event} from the story") { storyEvent: StoryEvent ->
            if (storyEvent.projectId == null) return@Given
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenDeleteStoryEventDialogHasBeenOpened(listOf(storyEvent))
                .confirm()
            awaitWithTimeout(1000) { storyEvents.getStoryEventByName(storyEvent.name.value) == null }
        }


        When("I want to remove the {story event} from the story") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .openDeleteStoryEventDialog()
        }
        When("I show the ramifications of removing the {story event} from the story") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenDeleteStoryEventDialogHasBeenOpened(listOf(storyEvent))
                .showRamifications()
        }
        When("I confirm I want to remove the {story event} from the story") { storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteStoryEventDialogOrError()
                .confirm()
            runBlocking { delay(100) } // give events time to propagate
        }
        When("I remove the {story event} from the story") { storyEvent: StoryEvent ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(storyEvent)
                .openDeleteStoryEventDialog()

            workbench
                .getOpenDeleteStoryEventDialogOrError()
                .confirm()
            runBlocking { delay(100) } // give events time to propagate
        }


        Then("I should be prompted to confirm removing the {story event} from the story") { storyEvent: StoryEvent ->
            val dialog = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteStoryEventDialog()
            Assertions.assertNotNull(dialog)
        }
        Then("the {story event} should not have been removed from the story") { storyEvent: StoryEvent ->
            Assertions.assertNotNull(storyEvent.projectId)
        }
        Then("the {story event} should have been removed from the story") { storyEvent: StoryEvent ->
            Assertions.assertNull(storyEvent.projectId)
        }
        Then(
            "the following should be listed as ramifications of removing the {story event} from the story"
        ) { storyEvent: StoryEvent, data: DataTable ->
            val ramifications = workbench.getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveStoryEventFromStoryRamificationsReportView::class)
                .content!!.uiComponent<RemoveStoryEventFromStoryRamificationsReportView>()!!

            val messages = ramifications.root.childrenUnmodifiable.map {
                it as TextFlow
                it.children.map {
                    when (it) {
                        is Text -> it.text
                        is Labeled -> it.text
                        else -> ""
                    }
                }.joinToString("")
            }
            data.asList().forEach {
                assertTrue(messages.contains(it)) {
                    """
                        Did not find expected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
        Then(
            "the following should not be listed as ramifications of removing the {story event} from the story"
        ) { storyEvent: StoryEvent, data: DataTable ->
            val ramifications = workbench.getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveStoryEventFromStoryRamificationsReportView::class)
                .content!!.uiComponent<RemoveStoryEventFromStoryRamificationsReportView>()!!

            val messages = ramifications.root.childrenUnmodifiable.map {
                it as TextFlow
                it.children.map {
                    when (it) {
                        is Text -> it.text
                        is Labeled -> it.text
                        else -> ""
                    }
                }.joinToString("")
            }
            data.asList().forEach {
                assertFalse(messages.contains(it)) {
                    """
                        Did not find expected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
    }

}