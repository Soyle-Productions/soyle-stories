package com.soyle.stories.core.storyevent

import com.soyle.stories.core.definitions.CoreTest
import kotlin.test.Test
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

class `Story Events Involve Characters` : CoreTest() {

    private val project = given.`a project`().`has been started`()

    @Test
    fun `No Characters are Involved in a new Story Event`() {
        val storyEvent = `when`.`a story event`().`is created in the`(project)

        then.the(storyEvent).`should not involve any characters`()
    }

    @Test
    fun `Can Involve Character in Story Event`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`().`has been created in the`(project)
            character = and.`a character`() `has been created in the` project
        }

        val availableCharacters = `when`.`the user`() `lists the available characters to involve in the` storyEvent

        then the availableCharacters `should have an item for the` character
    }

    @Test
    fun `Involve Character in Story Event`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`().`has been created in the`(project)
            character = and.`a character`() `has been created in the` project
        }

        `when` the character `is involved in the` storyEvent

        then the storyEvent `should involve the` character
    }

    @Test
    fun `Character is No Longer Available After Involvement`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`().`has been created in the`(project)
            character = and.`a character`() `has been created in the` project
            and the character `has been involved in the` storyEvent
        }

        val availableCharacters = `when`.`the user`() `lists the available characters to involve in the` storyEvent

        then the availableCharacters `should not have an item for the` character
    }

    @Test
    fun `Stop Involving Character in Story Event`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`() `has been created in the` project
            character = and.`a character`() `has been created in the` project
            and the character `has been involved in the` storyEvent
        }

        `when` the character `is no longer involved in the` storyEvent

        then.the(storyEvent).`should not involve any characters`()
    }

}