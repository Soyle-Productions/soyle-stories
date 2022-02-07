package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.mockk.InternalPlatformDsl.toStr
import org.junit.jupiter.api.Assertions.assertTrue

class `Involved Character Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private val storyEvents: `Story Event Robot`
        get() = `Story Event Robot`.invoke(soyleStories.getAnyOpenWorkbenchOrError())

    private fun givens() {
        Given(
            "I have involved the {character} in the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            storyEvents.givenStoryEventInvolvesCharacter(storyEvent, character)
        }
        Given(
            "I have stopped involving the {character} in the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            storyEvents.givenStoryEventDoesNotInvolveCharacter(storyEvent, character)
        }
        Given(
            "I have involved the {character} in the following story events"
        ) { character: Character, data: DataTable ->
            data.asList().forEach {
                val storyEvent = storyEvents.givenStoryEventExists(withName = NonBlankString.create(it)!!)
                storyEvents.givenStoryEventInvolvesCharacter(storyEvent, character)
            }
        }
    }

    private fun whens() {
        When(
            "I involve the {character} in the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventDetailsHaveBeenOpenedFor(storyEvent.id)
                .involveCharacter(character.id)
        }
    }

    private fun thens() {
        Then("the {story event} should not involve any characters") { storyEvent: StoryEvent ->
            assertTrue(storyEvent.involvedCharacters.isEmpty()) { "Expected to be empty.  Found: ${storyEvent.involvedCharacters}" }
        }
        Then(
            "the {story event} should involve the {character}"
        ) { storyEvent: StoryEvent, character: Character ->
            assertTrue(storyEvent.involvedCharacters.containsEntityWithId(character.id)) {
                "Story event does not involve character"
            }
        }
        Then(
            "the {character} should( still) be involved in the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            assertTrue(storyEvent.involvedCharacters.containsEntityWithId(character.id)) {
                "Story event does not involve character"
            }
        }
        Then(
            "the {character} should not have been removed from the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            assertTrue(storyEvent.involvedCharacters.containsEntityWithId(character.id)) {
                "Story event should still involve character"
            }
        }
    }

}