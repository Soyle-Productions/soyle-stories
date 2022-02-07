package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.RoleInScene
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Characters may have a Role in a Scene` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`().`has been created in the`(project)
    private val storyEvent = given.`a story event`(coveredBy = scene).`has been created in the`(project)
    private val character = given.`a character`().`has been created in the`(project)

    @Nested
    inner class `Rule - characters do not have a role by default` {

        @Test
        fun `Involve character in covered story event`() {
            `when` the character `is involved in the` storyEvent

            then the character `in the` scene `should not have a role` `-`
        }

    }

    @Test
    fun `List Characters in Scene After Assigning a Role`() {
        given the character `has been involved in the` storyEvent
        given the character `in the` scene `has been assigned to be the` RoleInScene.IncitingCharacter

        val `scene characters` = `when`.`the user`() `lists the characters in the` scene

        then the character `in the` `scene characters` `should have the role` RoleInScene.IncitingCharacter
    }

    @Nested
    inner class `Rule - a character can only have one role in a scene` {

        @Test
        fun `Assign Character Another Role`() {
            given the character `has been involved in the` storyEvent
            given the character `in the` scene `has been assigned to be the` RoleInScene.IncitingCharacter

            `when` the character `in the` scene `is assigned to be the` RoleInScene.OpponentCharacter

            then the character `in the` scene `should be the` RoleInScene.OpponentCharacter
        }

    }

    @Nested
    inner class `Rule - there can only be one inciting character per scene` {

        @Test
        fun `Assign Other Character as Inciting Character`() {
            val otherCharacter: Character.Id
            with(given) {
                given the character `has been involved in the` storyEvent
                and the character `in the` scene `has been assigned to be the` RoleInScene.IncitingCharacter
                otherCharacter = and.`a character`() `has been created in the` project
                and the otherCharacter `has been involved in the` storyEvent
            }

            `when` the otherCharacter `in the` scene `is assigned to be the` RoleInScene.IncitingCharacter

            then the character `in the` scene `should not have a role` `-`
            then the otherCharacter `in the` scene `should be the` RoleInScene.IncitingCharacter
        }

    }

}