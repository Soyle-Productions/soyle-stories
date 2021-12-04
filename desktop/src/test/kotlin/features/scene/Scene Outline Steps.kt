package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.outline.SceneOutlineAssertions.Companion.assertThat
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import io.cucumber.java8.En
import junit.framework.Assert.assertTrue

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
        When("I remove the {story event} from the {scene} outline") { storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)

                .removeStoryEvent(storyEvent.id)
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
        Then("the {scene} should not cover a story event") { scene: Scene ->
            assertTrue(
                "Expected ${scene.coveredStoryEvents} to be empty",
                scene.coveredStoryEvents.isEmpty()
            )
        }
    }

}