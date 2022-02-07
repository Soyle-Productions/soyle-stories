package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.scene.addStoryEvent
import com.soyle.stories.desktop.config.drivers.scene.givenFocusedOn
import com.soyle.stories.desktop.config.drivers.scene.givenSceneOutlineToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.coverSelectedStoryEventIn
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventHasBeenSelected
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventListToolHasBeenOpened
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions

class `Cover Story Event in Scene Steps` : StoryEventFeatureSteps {

    init {
        Given("I have covered the {story event} in the {scene}") { storyEvent: StoryEvent, scene: Scene ->
            if (storyEvent.sceneId == scene.id) return@Given
            coverStoryEventInScene(storyEvent, scene)
        }


        When("I cover the {story event} in the {scene}") { storyEvent: StoryEvent, scene: Scene ->
            coverStoryEventInScene(storyEvent, scene)
        }


        Then("the {story event} should be covered by the {scene}") { storyEvent: StoryEvent, scene: Scene ->
            Assertions.assertEquals(scene.id, storyEvent.sceneId)
        }
    }

    private fun coverStoryEventInScene(storyEvent: StoryEvent, scene: Scene) {
        workbench
            .givenStoryEventListToolHasBeenOpened()
            .givenStoryEventHasBeenSelected(storyEvent)
            .coverSelectedStoryEventIn(scene.id)
    }

}