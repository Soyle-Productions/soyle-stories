package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.confirm
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.showRamifications
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenRamificationsToolOrError
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenReportOrError
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.outline.SceneOutlineAssertions.Companion.assertThat
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportView
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.Labeled
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import org.junit.jupiter.api.Assertions
import tornadofx.uiComponent

class `Scene Outline Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I am viewing the {scene} outline") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
    }

    private fun whens() {
        When("I view the {scene} outline") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .openSceneOutlineTool(scene)
        }
        When("I add the {story event} to the {scene} outline") { storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)

                .addStoryEvent(storyEvent.id)
        }
        When(
            "I show the ramifications of removing the {story event} from the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenConfirmRemoveStoryEventFromSceneDialogHasBeenOpened(storyEvent.id)
                .showRamifications()
        }
        When("I remove the {story event} from the {scene} outline") { storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)
                .openConfirmRemoveStoryEventFromSceneDialog(storyEvent.id)

            getOpenConfirmRemoveStoryEventFromSceneDialog(scene.id, storyEvent.id)
                ?.confirm()
            runBlocking {
                delay(1000)
                awaitPulse() } //
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun thens() {
        Then(
            "the {story event} should be listed in the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene ->
            val sceneOutline = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)

            assertThat(sceneOutline) {
                hasStoryEvent(storyEvent)
            }
        }
        Then(
            "the {scene} outline should not list a story event named {string}"
        ) { scene: Scene, storyEventName: String ->
            val sceneOutline = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)

            assertThat(sceneOutline) {
                doesNotHaveStoryEventNamed(storyEventName)
            }
        }
        Then(
            "the {story event} should not be listed in the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene ->
            val sceneOutline = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)

            assertThat(sceneOutline) {
                doesNotHaveStoryEvent(storyEvent)
            }
        }
        Then(
            "the following should be listed as ramifications of removing the {story event} from the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene, data: DataTable ->
            val ramifications = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRamificationsToolOrError()
                .getOpenReportOrError(UncoverStoryEventRamificationsReportView::class)
                .content!!.uiComponent<UncoverStoryEventRamificationsReportView>()!!

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
                Assertions.assertTrue(messages.contains(it)) {
                    """
                        Did not find expected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
        Then(
            "the following should not be listed as ramifications of removing the {story event} from the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene, data: DataTable ->
            val ramifications = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRamificationsToolOrError()
                .getOpenReportOrError(UncoverStoryEventRamificationsReportView::class)
                .content!!.uiComponent<UncoverStoryEventRamificationsReportView>()!!

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
                Assertions.assertFalse(messages.contains(it)) {
                    """
                        Found unexpected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
    }

}