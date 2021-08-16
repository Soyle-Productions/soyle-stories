package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.`Story Event Robot`
import com.soyle.stories.desktop.config.drivers.storyevent.createStoryEventNamed
import com.soyle.stories.desktop.config.drivers.storyevent.givenCreateStoryEventDialogHasBeenOpened
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventListToolHasBeenOpened
import com.soyle.stories.desktop.config.features.soyleStories
import io.cucumber.java8.En
import org.junit.jupiter.api.fail

class `Story Event Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    val storyEvents: `Story Event Robot`
        get() = `Story Event Robot`(soyleStories.getAnyOpenWorkbenchOrError())

    private fun givens() {

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
        }
    }

    private fun thens() {
        Then("a story event named {string} should have been created") { expectedName: String ->
            val storyEvent = storyEvents.getStoryEventByName(expectedName)
                ?: fail("Story event named \"$expectedName\" was not created")

            TODO("Ensure listed in story event list")
        }
    }

}