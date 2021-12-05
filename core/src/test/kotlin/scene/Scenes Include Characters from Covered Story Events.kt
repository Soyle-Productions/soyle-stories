package com.soyle.stories.core.scene

import com.soyle.stories.core.IntTest
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class `Scenes Include Characters from Covered Story Events` : IntTest() {

    private val project = given.`a project`().`has been started`()

    @Test
    fun `No Characters are Included in a new Scene`() {
        val scene = `when`.`a scene`() `is created in the` project

        then.the(scene).`should not include any characters`()
    }

    @Nested
    inner class `Include Characters in Scene` {

        private val scene = given.`a scene`() `has been created in the` project
        private val storyEvent = given.`a story event`() `has been created in the` project
        private val character = given.`a character`() `has been created in the` project

        @Test
        fun `Cover Story Event with Involved Character`() {
            given the character `has been involved in the` storyEvent

            `when` the storyEvent `is covered by the` scene

            then the scene `should include the` character
        }

        @Test
        fun `Involve Character in Covered Story Event`() {
            given the storyEvent `has been covered by the` scene

            `when` the character `is involved in the` storyEvent

            then the scene `should include the` character
        }

    }

    @Nested
    inner class `Remove Character from Scene` {

        private val scene = given.`a scene`() `has been created in the` project
        private val storyEvent = given.`a story event`() `has been created in the` project
        private val character = given.`a character`() `has been created in the` project
        init {
            given the character `has been involved in the` storyEvent
            given the storyEvent `has been covered by the` scene
        }

        @Test
        fun `Remove Character from Covered Story Event`() {
            `when` the character `is no longer involved in the` storyEvent

            then.the(scene).`should not include any characters`()
        }

        @Test
        fun `Remove Covered Story Event with Involved Character`() {
            `when`.the(storyEvent).`is uncovered`()

            then.the(scene).`should not include any characters`()
        }

        @Test
        fun `Remove Included Character from Story`() {
            `when` the character `is removed from the` project

            then.the(scene).`should not include any characters`()
        }

        @Test
        fun `Delete Covered Story Event with Involved Character`() {
            `when`.the(storyEvent).`is deleted`()

            then.the(scene).`should not include any characters`()
        }

    }

}