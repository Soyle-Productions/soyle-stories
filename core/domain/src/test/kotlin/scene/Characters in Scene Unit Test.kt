package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.scene.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneCleared
import com.soyle.stories.domain.scene.events.SceneEvent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DynamicTest.dynamicTest

class `Characters in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    @Test
    fun `a character can be included in a scene`() {
        val update = scene.withCharacterIncluded(character)
        update as Updated
        update.event.sceneId.mustEqual(scene.id)
        update.event.characterInScene.characterId.mustEqual(character.id)
        update.event.characterInScene.characterName.mustEqual(character.name.value)
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character cannot be included in a scene more than once`() {
        val update = scene.withCharacterIncluded(character).scene.withCharacterIncluded(character)
        update as WithoutChange
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character in a scene can be renamed to match the name of the source character`() {
        val newName = nonBlankStr("New Name")
        val update = scene.withCharacterIncluded(character).scene.withCharacterRenamed(character.withName(newName))
        update as Updated
        update.event.sceneId.mustEqual(scene.id)
        update.event.renamedCharacter.characterId.mustEqual(character.id)
        update.event.renamedCharacter.characterName.mustEqual(newName.value)
        update.scene.includedCharacters.find { it.characterId == character.id }!!.characterName.mustEqual(newName.value)
        update.scene.includesCharacter(character.id).mustEqual(true)
    }

    @Test
    fun `a character in a scene renamed with the same name should emit no update`() {
        val update = scene.withCharacterIncluded(character).scene.withCharacterRenamed(character)
        update as WithoutChange
        update.scene.id.mustEqual(scene.id)
        update.scene.includedCharacters.find { it.characterId == character.id }!!.characterName.mustEqual(character.name.value)
    }

    @Test
    fun `a character not in a scene cannot be renamed in the scene`() {
        val error = assertThrows<SceneDoesNotIncludeCharacter> {
            scene.withCharacterRenamed(character)
        }
        error.sceneId.mustEqual(scene.id)
        error.characterId.mustEqual(character.id)
    }

    @Nested
    inner class `Character Role in Scene` {

        @Test
        fun `a character should not have a role initially`() {
            val defaultRole = scene.withCharacterIncluded(character)
                .scene.includedCharacters.getOrError(character.id)
                .roleInScene
            assertNull(defaultRole)
        }

        @Test
        fun `can assign a new role`() {
            val newRole = RoleInScene.IncitingCharacter
            val roleUpdate = scene.withCharacterIncluded(character)
                .scene.withRoleForCharacter(character.id, newRole)

            roleUpdate.scene.includedCharacters.getOrError(character.id).roleInScene.mustEqual(newRole)
            roleUpdate as Updated
            roleUpdate.event.events.single().mustEqual(CharacterAssignedRoleInScene(scene.id, character.id, newRole))
        }

        @Test
        fun `can clear a character's role`() {
            val roleUpdate = scene.withCharacterIncluded(character)
                .scene.withRoleForCharacter(character.id, RoleInScene.IncitingCharacter)
                .scene.withRoleForCharacter(character.id, null)

            assertNull(roleUpdate.scene.includedCharacters.getOrError(character.id).roleInScene)
            roleUpdate as Updated
            roleUpdate.event.events.single().mustEqual(CharacterRoleInSceneCleared(scene.id, character.id))
        }

        @Test
        fun `cannot update character's role if they aren't in the scene`() {
            val error = assertThrows<SceneDoesNotIncludeCharacter> {
                scene.withRoleForCharacter(character.id, RoleInScene.IncitingCharacter)
            }
            error.characterId.mustEqual(character.id)
            error.sceneId.mustEqual(scene.id)
        }

        @Test
        fun `assigning the same role should not produce an update`() {
            listOf(
                scene.withCharacterIncluded(character)
                    .scene.withRoleForCharacter(character.id, null),

                scene.withCharacterIncluded(character)
                    .scene.withRoleForCharacter(character.id, RoleInScene.IncitingCharacter)
                    .scene.withRoleForCharacter(character.id, RoleInScene.IncitingCharacter),

                scene.withCharacterIncluded(character)
                    .scene.withRoleForCharacter(character.id, RoleInScene.OpponentCharacter)
                    .scene.withRoleForCharacter(character.id, RoleInScene.OpponentCharacter)
            ).forEachIndexed { index, roleUpdate ->
                if (roleUpdate !is WithoutChange) {
                    error("Should not have received update for $index test")
                }
            }
        }

        @Nested
        inner class `Assign Second Inciting Character` {

            val secondCharacter = makeCharacter()
            val initialScene = scene.withCharacterIncluded(character).scene
                .withCharacterIncluded(secondCharacter).scene
                .withRoleForCharacter(character.id, RoleInScene.IncitingCharacter).scene

            @Test
            fun `should clear previous inciting character's role`() {
                val sceneUpdate = initialScene.withRoleForCharacter(secondCharacter.id, RoleInScene.IncitingCharacter)

                assertNull(sceneUpdate.scene.includedCharacters.getOrError(character.id).roleInScene)
            }

            @Test
            fun `should output role cleared event and role assignment event`() {
                val sceneUpdate = initialScene.withRoleForCharacter(secondCharacter.id, RoleInScene.IncitingCharacter)

                sceneUpdate as Updated
                sceneUpdate.event.events.first().mustEqual(CharacterRoleInSceneCleared(scene.id, character.id))
                sceneUpdate.event.events.component2().mustEqual(
                    CharacterAssignedRoleInScene(
                        scene.id,
                        secondCharacter.id,
                        RoleInScene.IncitingCharacter
                    )
                )
                sceneUpdate.event.events.size.mustEqual(2)
            }

        }

    }

    @Nested
    inner class `Character Desire in Scene`
    {

        @Test
        fun `characters have no desire when first included`() {
            scene.withCharacterIncluded(character).scene
                .includedCharacters.getOrError(character.id)
                .desire.mustEqual("")
        }

        @Test
        fun `cannot update character desire if character is not included`() {
            val error = assertThrows<SceneDoesNotIncludeCharacter> {
                scene.withDesireForCharacter(character.id, "New Desire")
            }
            error.sceneId.mustEqual(scene.id)
            error.characterId.mustEqual(character.id)
        }

        @Test
        fun `can update included character's desire`() {
            val update = scene.withCharacterIncluded(character).scene
                .withDesireForCharacter(character.id, "New Desire")
            update as Updated
            update.event.mustEqual(CharacterDesireInSceneChanged(scene.id, character.id, "New Desire"))
            update.scene.includedCharacters.getOrError(character.id).desire.mustEqual("New Desire")
        }

        @Test
        fun `passing in same desire should return no update`() {
            val update = scene.withCharacterIncluded(character).scene
                .withDesireForCharacter(character.id, "New Desire").scene
                .withDesireForCharacter(character.id, "New Desire")
            update as WithoutChange
        }

    }
}