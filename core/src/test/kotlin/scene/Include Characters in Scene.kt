package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import com.soyle.stories.domain.scene.character.RoleInScene
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Include Characters in Scene` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`(named = "Big Battle").`has been created in the`(project)
    private val character = given.`a character`().`has been created in the`(project)

    private val storyEvent = then.`a story event`(named = "Big Battle").`should have been created in`(project)

    @Test
    fun `Character Not Yet Involved in Covered Story Event`() {
        val availableCharacters = `when`.`the user`().`lists the available characters to include in the`(scene)

        then.the(availableCharacters).`should include the`(character)
    }

    @Test
    fun `Character has been Involved in Covered Story Event`() {
        given.the(character).`has been involved in the`(storyEvent)

        val availableCharacters = `when`.`the user`().`lists the available characters to include in the`(scene)

        then.the(availableCharacters).`should not include any characters`()
    }

    @Test
    fun `Select a Character to Include in Scene`() {
        `when`.the(character).`is included in the`(scene)

        then.the(scene).`should include the`(character)
    }

    @Test
    fun `List Story Events in Scene without Any Covered Story Events`() {
        given.the(storyEvent).`has been removed from the story`()

        val storyEventsInScene = `when`.`the user`().`lists the story events covered by the`(scene)

        then.the(storyEventsInScene).`should not include any story events`()
    }

    @Test
    fun `List Story Events with Covered Story Events in Scene`() {
        val firstStoryEvent = storyEvent
        val secondStoryEvent = given.`a story event`(named = "Something happens").`has been created in the`(project)
        val thirdStoryEvent = given.`a story event`(named = "Something else happens").`has been created in the`(project)
        given.the(secondStoryEvent).`has been covered by the`(scene)
        given.the(thirdStoryEvent).`has been covered by the`(scene)

        val storyEventsInScene = `when`.`the user`().`lists the story events covered by the`(scene)

        then.the(storyEventsInScene).`should include the`(firstStoryEvent, secondStoryEvent, and = thirdStoryEvent)
    }

    @Test
    fun `Involve Character from Scene in Multiple Story Events`() {
        val firstStoryEvent = storyEvent
        val secondStoryEvent = given.`a story event`(named = "Something happens").`has been created in the`(project)
        val thirdStoryEvent = given.`a story event`(named = "Something else happens").`has been created in the`(project)
        given.the(secondStoryEvent).`has been covered by the`(scene)
        given.the(thirdStoryEvent).`has been covered by the`(scene)
        given.the(character).`has been included in the`(scene)

        `when`.the(character).`is involved in the`(firstStoryEvent, and = secondStoryEvent)

        then.the(firstStoryEvent).`should involve the`(character)
        then.the(secondStoryEvent).`should involve the`(character)
    }

    @Test
    fun `Involve Character from Scene in Multiple Story Events and Create Story Event`() {
        given.the(character).`has been included in the`(scene)

        `when`.the(character).`is involved in the`(storyEvent)
        val newStoryEvent = `when`.`a story event`(
            named = "Something happens",
            atTime = 5,
            coveredBy = scene
        ).`is created in the`(project)
        `when`.the(character).`is involved in the`(newStoryEvent)

        with(then) {
            then.`a story event`(named = "Something happens").`should have been created in`(project)
            and.the(newStoryEvent).`should be at time`(5)
            and.the(newStoryEvent).`should be covered by the`(scene)
            and.the(storyEvent).`should involve the`(character)
            and.the(newStoryEvent).`should involve the`(character)
        }
    }

    @Nested
    inner class `Rule - Associating additional data with an involved character should include that character in the scene` {

        @Test
        fun `Set the Role of an Involved Character in a Scene`() {
            given.the(character).`has been involved in the`(storyEvent)

            `when`.the(character).`in the`(scene).`is assigned to be the`(RoleInScene.IncitingCharacter)

            then.the(scene).`should include the`(character)
            then.the(character).`in the`(scene).`should be the`(RoleInScene.IncitingCharacter)
        }

        @Test
        fun `Set the Desire of an Involved Character in a Scene`() {
            given.the(character).`has been involved in the`(storyEvent)

            `when`.the(character).`in the`(scene).desires("Some desire")

            then.the(scene).`should include the`(character)
        }

        @Test
        fun `Set the Motivation of an Involved Character in a Scene`() {
            given.the(character).`has been involved in the`(storyEvent)

            `when`.the(character).`in the`(scene).`is motivated by`("A traumatic event")

            then.the(scene).`should include the`(character)
        }

    }

}