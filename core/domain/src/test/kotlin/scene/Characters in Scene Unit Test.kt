package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.*
import com.soyle.stories.domain.scene.character.events.CharacterDesireInSceneChanged
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import com.soyle.stories.domain.scene.character.exceptions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class `Characters in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    @Nested
    inner class `Include Character in Scene` {

        @Test
        fun `character can be included in scene`() {
            // when
            val update = scene.withCharacterIncluded(character)
            // then
            update as Successful
            update.event.mustEqual(CharacterIncludedInScene(scene.id, character.id, character.displayName.value, scene.projectId))
        }

        @Test
        fun `character cannot be included in scene twice`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
            // when
            val update = scene.withCharacterIncluded(character)
            // then
            update as UnSuccessful
            update.reason.mustEqual(CharacterAlreadyIncludedInScene(scene.id, character.id))
        }

    }

    @Nested
    inner class `Remove Character from Scene` {

        @Test
        fun `cannot remove character that hasn't been included`() {
            val update = scene.withCharacter(character.id)?.removed() as? Successful<*>
            // then
            assertNull(update)
        }

        @Test
        fun `can remove included character`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
            // when
            val update = scene.withCharacter(character.id)?.removed() as? Successful<*>
            // then
            update!!.event.mustEqual(CharacterRemovedFromScene(scene.id, character.id))
        }

        @Test
        fun `cannot remove included character twice`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.removed()
            // when
            val update = scene.withCharacter(character.id)?.removed()
            // then
            assertNull(update)
        }

    }

    @Nested
    inner class `Assign Role to Character in Scene` {

        @Test
        fun `cannot update character's role if they aren't in the scene`() {
            // when
            val update = scene.withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter) as? Successful
            // then
            assertNull(update)
        }

        @Test
        fun `can update included character's role`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
            // when
            val update = scene.withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter) as? Successful
            // then
            update!!.event.events.single().mustEqual(CharacterAssignedRoleInScene(scene.id, character.id, RoleInScene.IncitingCharacter))
        }

        @Test
        fun `can clear a character's role`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.assignedRole(RoleInScene.OpponentCharacter)
            // when
            val update = scene.withCharacter(character.id)?.assignedRole(null)
            // then
            update as Successful
            update.event.events.single().mustEqual(CharacterRoleInSceneCleared(scene.id, character.id))
        }

        @ParameterizedTest
        @ValueSource(strings = ["IncitingCharacter", "OpponentCharacter", "null"])
        fun `assigning the same role to a character should not produce an update`(roleString: String) {
            val role = kotlin.runCatching { RoleInScene.valueOf(roleString) }.getOrNull()
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.assignedRole(role)
            // when
            val update = scene.withCharacter(character.id)?.assignedRole(role)
            // then
            update as UnSuccessful
            if (role == null) {
                update.reason.mustEqual(CharacterAlreadyDoesNotHaveRoleInScene(scene.id, character.id))
            } else {
                update.reason.mustEqual(CharacterAlreadyHasRoleInScene(scene.id, character.id, role))
            }
        }

        @Nested
        inner class `Assign Second Inciting Character` {

            private val secondCharacter = makeCharacter()

            @Test
            fun `initial inciting character's role should be cleared`() {
                // given
                val (scene) = scene.withCharacterIncluded(character)
                    .scene.withCharacterIncluded(secondCharacter)
                    .scene.withCharacter(character.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                // when
                val update = scene.withCharacter(secondCharacter.id)?.assignedRole(RoleInScene.IncitingCharacter)
                // then
                update as Successful
                update.event.events[0].mustEqual(CharacterRoleInSceneCleared(scene.id, character.id))
            }

            @Test
            fun `new inciting character should have role`() {
                // given
                val (scene) = scene.withCharacterIncluded(character)
                    .scene.withCharacterIncluded(secondCharacter)
                    .scene.withCharacter(character.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                // when
                val update = scene.withCharacter(secondCharacter.id)?.assignedRole(RoleInScene.IncitingCharacter)
                // then
                update as Successful
                update.event.events[1].mustEqual(CharacterAssignedRoleInScene(scene.id, secondCharacter.id, RoleInScene.IncitingCharacter))
            }

        }

    }

    @Nested
    inner class `Change Character Design in Scene` {

        @Test
        fun `cannot update character desire if character is not included`() {
            // when
            val update = scene.withCharacter(character.id)?.desireChanged("New Desire")
            // then
            assertNull(update)
        }

        @Test
        fun `can update included character's desire`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
            // when
            val update = scene.withCharacter(character.id)?.desireChanged("New Desire")
            // then
            update as Successful
            update.event.mustEqual(CharacterDesireInSceneChanged(scene.id, character.id, "New Desire"))
        }

        @Test
        fun `passing in same desire should return no update`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.desireChanged("New Desire")
            // when
            val update = scene.withCharacter(character.id)?.desireChanged("New Desire")
            // then
            update as UnSuccessful
            update.reason.mustEqual(CharacterInSceneAlreadyHasDesire(scene.id, character.id, "New Desire"))
        }

    }

    @Nested
    inner class `Change Character Motivation in Scene` {

        @Test
        fun `cannot update character motivation if character is not included`() {
            // when
            val update = scene.withCharacter(character.id)?.motivationChanged("New Motivation")
            // then
            assertNull(update)
        }

        @Test
        fun `can update included character's motivation`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
            // when
            val update = scene.withCharacter(character.id)?.motivationChanged("New Motivation")
            // then
            update as Successful
            update.event.mustEqual(CharacterGainedMotivationInScene(scene.id, character.id, "New Motivation"))
        }

        @Test
        fun `passing in same motivation should return no update`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("New Motivation")
            // when
            val update = scene.withCharacter(character.id)?.motivationChanged("New Motivation")
            // then
            update as UnSuccessful
            update.reason.mustEqual(CharacterInSceneAlreadyHasMotivation(scene.id, character.id, "New Motivation"))
        }

        @Test
        fun `can clear include character's motivation`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("New Motivation")
            // when
            val update = scene.withCharacter(character.id)?.motivationChanged(null)
            // then
            update as Successful
            update.event.mustEqual(CharacterMotivationInSceneCleared(scene.id, character.id))
        }

        @Test
        fun `clearing the character's motivation twice should return no update`() {
            // given
            val (scene) = scene.withCharacterIncluded(character)
                .scene.withCharacter(character.id)!!.motivationChanged("New Motivation")
                .scene.withCharacter(character.id)!!.motivationChanged(null)
            // when
            val update = scene.withCharacter(character.id)?.motivationChanged(null)
            // then
            update as UnSuccessful
            update.reason.mustEqual(CharacterInSceneAlreadyDoesNotHaveMotivation(scene.id, character.id))
        }

    }
}