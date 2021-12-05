package com.soyle.stories.core.storyevent

import com.soyle.stories.core.IntTest
import kotlin.test.Test
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent

class `Story Events Involve Characters` : IntTest() {

    private val project = given.`a project`().`has been started`()

    @Test
    fun `No Characters are Involved in a new Story Event`() {
        val storyEvent = `when`.`a story event`().`is created in the`(project)

        then.the(storyEvent).`should not involve any characters`()
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
    fun `Delete Character Involved in Story Event`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`().`has been created in the`(project)
            character = and.`a character`() `has been created in the` project
            and the character `has been involved in the` storyEvent
        }

        `when` the character `is removed from the` project

        then.the(storyEvent).`should not involve any characters`()
    }

    @Test
    fun `Stop Involving Character in Story Event`() {
        val storyEvent: StoryEvent.Id
        val character: Character.Id

        with(given) {
            storyEvent = given.`a story event`().`has been created in the`(project)
            character = and.`a character`() `has been created in the` project
            and the character `has been involved in the` storyEvent
        }

        `when` the character `is no longer involved in the` storyEvent

        then.the(storyEvent).`should not involve any characters`()
    }

    /*


  Scenario: Stop Involving Character in Story Event
    Given I have created a story event named "Something Happens"
    And I have created a character named "Bob"
    And I have involved the "Bob" character in the "Something Happens" story event
    When I stop involving the "Bob" character in the "Something Happens" story event
    Then the "Something Happens" story event should not involve any characters
     */

}