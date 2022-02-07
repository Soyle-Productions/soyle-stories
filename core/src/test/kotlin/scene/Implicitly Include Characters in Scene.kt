package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import org.junit.jupiter.api.Test

class `Implicitly Include Characters in Scene` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`().`has been created in the`(project)
    private val character = given.`a character`().`has been created in the`(project)

    @Test
    fun `Involve a Character in a Covered Story Event`() {
        val storyEvent = given.`a story event`(coveredBy = scene).`has been created in the`(project)
        given.the(character).`has been involved in the`(storyEvent)

        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(sceneCharacters).`should include the`(character)
    }

    @Test
    fun `Cover Story Event with Involved Character`() {
        val storyEvent = given.`a story event`().`has been created in the`(project)
        given.the(character).`has been involved in the`(storyEvent)
        given.the(storyEvent).`has been covered by the`(scene)

        val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

        then.the(sceneCharacters).`should include the`(character)
    }

}