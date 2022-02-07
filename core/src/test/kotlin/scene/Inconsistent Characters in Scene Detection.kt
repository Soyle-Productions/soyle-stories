package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class `Inconsistent Characters in Scene Detection` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    private val character = given.`a character`().`has been created in the`(project)
    private val scene = given.`a scene`(named = "Big Battle").`has been created in the`(project)
    private val storyEvent = then.`a story event`(named = "Big Battle").`should have been created in`(project)

    init {
        given.the(character).`has been included in the`(scene)
    }

    @Test
    fun `Include Character without a Backing Story Event`() {
        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(character).`in the`(sceneCharacters).`should not have any story event coverage`()
    }

    @Test
    fun `Remove a Covered Story Event with Involved Character`() {
        given.the(character).`has been involved in the`(storyEvent)
        given.the(storyEvent).`has been uncovered`()

        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(character).`in the`(sceneCharacters).`should not have any story event coverage`()
    }

    @Test
    fun `Remove Included Character from Story`() {
        given.the(character).`has been removed from the`(project)

        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(character).`in the`(sceneCharacters).`should be flagged as removed from the story`()
    }

    @Test
    fun `Delete Covered Story Event with Involved Character`() {
        given.the(character).`has been involved in the`(storyEvent)
        given.the(storyEvent).`has been removed from the story`()

        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(character).`in the`(sceneCharacters).`should not have any story event coverage`()
    }

}