package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.storyevent.getOpenStoryEventListToolOrError
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.storyevent.openStoryEventListTool
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.storyevent.StoryEvent
import io.cucumber.datatable.DataTable
import org.junit.jupiter.api.Assertions

class `List All Story Events in Story Steps` : StoryEventFeatureSteps {

    init {
        Given("I have listed all the story events in my story") {
            workbench.givenStoryEventListToolHasBeenOpened()
        }


        When("I list all the story events in my story") {
            workbench.openStoryEventListTool()
        }


        Then("there should not be any story events listed in my story") {
            workbench
                .getOpenStoryEventListToolOrError()
                .assertThis {
                    hasNoStoryEvents()
                }
        }
        Then("I should be prompted to create my first story event in my story") {
            workbench
                .getOpenStoryEventListToolOrError()
                .assertThis {
                    isShowingWelcomePrompt()
                }
        }
        Then("the {story event} should be listed in my story") { storyEvent: StoryEvent ->
            workbench
                .getOpenStoryEventListToolOrError()
                .assertThis {
                    hasStoryEvent(storyEvent)
                }
        }
        Then(
            "all the following story events should be listed in my story in the following order"
        ) { data: DataTable ->
            val storyEvents = data.asMaps().map {
                val storyEvent = storyEvents.getStoryEventByName(it["Name"]!!)!!
                Assertions.assertEquals(it["Time"]!!.toLong(), storyEvent.time.toLong())
                storyEvent
            }
            workbench
                .getOpenStoryEventListToolOrError()
                .assertThis {
                    storyEvents.forEach { hasStoryEvent(it) }
                    hasOrder(storyEvents.map { it.name.value })
                }
        }
        Then("there should not be a story event named {string} listed in my story") { nameThatShouldNotExist: String ->
            workbench
                .getOpenStoryEventListToolOrError()
                .assertThis {
                    doesNotHaveStoryEventNamed(nameThatShouldNotExist)
                }
        }
    }

}