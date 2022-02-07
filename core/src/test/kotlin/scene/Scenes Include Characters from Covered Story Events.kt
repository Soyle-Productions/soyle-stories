@file:Suppress("LocalVariableName")

package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import com.soyle.stories.domain.scene.character.RoleInScene.IncitingCharacter
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class `Scenes Include Characters from Covered Story Events` : CoreTest() {

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

            then the scene `should not include the` character
        }

        @Test
        fun `Involve Character in Covered Story Event`() {
            given the storyEvent `has been covered by the` scene

            `when` the character `is involved in the` storyEvent

            then the scene `should not include the` character
        }

        @Test
        fun `List Characters in Scene After Covering Story Event with Involved Character`() {
            given the storyEvent `has been covered by the` scene
            given the character `has been involved in the` storyEvent

            val `scene characters` = `when`.`the user`() `lists the characters in the` scene

            then the `scene characters` `should include the` character
        }

        @Test
        fun `Assign Role to Involved Character in the Scene`() {
            given the character `has been involved in the` storyEvent
            given the storyEvent `has been covered by the` scene

            `when` the character `in the` scene `is assigned to be the` IncitingCharacter

            then the scene `should include the` character
            then the character `in the` scene `should be the` IncitingCharacter
        }

        @Test
        fun `Set Desire for Involved Character in the Scene`() {
            given the character `has been involved in the` storyEvent
            given the storyEvent `has been covered by the` scene

            `when` the character `in the` scene desires "Get dat bread"

            then the scene `should include the` character
            then the character `in the` scene `should have the desire of` "Get dat bread"
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
            given the character `in the` scene `has been assigned to be the` IncitingCharacter
        }

        @Test
        fun `Remove Character from Covered Story Event`() {
            `when` the character `is no longer involved in the` storyEvent

            then.the(scene) `should include the` character
        }

        @Test
        fun `List Characters in Scene After Removing Character from Covered Story Event`() {
            given the character `has been removed from the` storyEvent

            val `scene characters` = `when`.`the user`() `lists the characters in the` scene

            then the `scene characters` `should include the` character
            then.the(character).`in the`(`scene characters`).`should not have any story event coverage`()
        }

        @Test
        fun `Remove Covered Story Event with Involved Character`() {
            `when`.the(storyEvent).`is uncovered`()

            then.the(scene) `should include the` character
        }

        @Test
        fun `List Characters in Scene After Uncovering Covered Story Event`() {
            given.the(storyEvent).`has been uncovered`()

            val `scene characters` = `when`.`the user`() `lists the characters in the` scene

            then the `scene characters` `should include the` character
            then.the(character).`in the`(`scene characters`).`should not have any story event coverage`()
        }

        @Test
        fun `Delete Covered Story Event with Involved Character`() {
            `when`.the(storyEvent).`is removed`()

            then.the(scene) `should include the` character
        }

        @Test
        fun `List Characters in Scene After Deleting Covered Story Event`() {
            given.the(storyEvent).`has been removed from the story`()

            val `scene characters` = `when`.`the user`() `lists the characters in the` scene

            then the `scene characters` `should include the` character
            then.the(character).`in the`(`scene characters`).`should not have any story event coverage`()
        }

    }

}