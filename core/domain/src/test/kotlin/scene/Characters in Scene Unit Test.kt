package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.*
import com.soyle.stories.domain.scene.character.exceptions.characterInSceneAlreadyHasDesire
import com.soyle.stories.domain.scene.character.exceptions.characterInSceneAlreadyHasName
import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.storyevent.events.CharacterInvolvedInStoryEvent
import com.soyle.stories.domain.storyevent.makeStoryEvent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DynamicTest.dynamicTest
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful

class `Characters in Scene Unit Test` {

    private val scene = makeScene(coveredStoryEvents = emptySet())
    private val character = makeCharacter()
    val storyEvent = makeStoryEvent()

    @Nested
    inner class `Include Character in Scene` {

        @Nested
        inner class `Via Response to Character Involved in Story Event` {

            @TestFactory
            fun `Given Scene Does not Cover any Story Events`(): List<DynamicTest> {
                val update = scene.withCharacterIncluded(
                    CharacterInvolvedInStoryEvent(
                        storyEvent.id, character.id, character.name.value
                    )
                )

                return listOf(
                    `character should not be included`(update), `no event should have been produced`(update)
                )
            }

            @TestFactory
            fun `Given Scene Covers Story Event`(): List<DynamicTest> {
                val update = scene.withStoryEvent(storyEvent).scene.withCharacterIncluded(
                    CharacterInvolvedInStoryEvent(
                        storyEvent.id, character.id, character.name.value
                    )
                )

                return listOf(dynamicTest("character should have been included") {
                    update.scene.includedCharacters.getOrError(character.id).run {
                        sceneId.mustEqual(scene.id)
                        characterName.mustEqual(character.name)
                    }
                }, dynamicTest("should produce character included event") {
                    update as Successful
                    update.event.mustEqual(
                        IncludedCharacterInScene(
                            scene.id, Scene.IncludedCharacter(character.id, character.name.value)
                        )
                    )
                })
            }

            @TestFactory
            fun `Given Scene Already Includes Character`(): List<DynamicTest> {
                val otherSource = makeStoryEvent()
                val update = scene.withStoryEvent(storyEvent)
                    .scene.withStoryEvent(otherSource)
                    .scene.withCharacterIncluded(
                        CharacterInvolvedInStoryEvent(
                            otherSource.id, character.id, character.name.value
                        )
                    ).scene.withCharacterIncluded(
                        CharacterInvolvedInStoryEvent(
                            storyEvent.id, character.id, character.name.value
                        )
                    )

                return listOf(dynamicTest("should produce character source added event") {
                    update as Successful
                    update.event.mustEqual(
                        CharacterInSceneSourceAdded(
                            scene.id, character.id, storyEvent.id
                        )
                    )
                })
            }

            @TestFactory
            fun `Given Scene Already Includes Character from the Story Event`(): List<DynamicTest> {
                val update = scene.withStoryEvent(storyEvent)
                    .scene.withStoryEvent(storyEvent)
                    .scene.withCharacterIncluded(
                        CharacterInvolvedInStoryEvent(
                            storyEvent.id, character.id, character.name.value
                        )
                    ).scene.withCharacterIncluded(
                        CharacterInvolvedInStoryEvent(
                            storyEvent.id, character.id, character.name.value
                        )
                    )

                return listOf(dynamicTest("should return duplicate op failure") {
                    update as UnSuccessful
                    update.reason.mustEqual(
                        CharacterInSceneAlreadySourcedFromStoryEvent(
                            scene.id,
                            character.id,
                            storyEvent.id
                        )
                    )
                })
            }

        }

        @Nested
        inner class `Via Covering Story Event with Involved Character` {

            @TestFactory
            fun `Given Story Event Involves Character when Covered by Scene`(): List<DynamicNode> {
                val storyEvent = storyEvent.withCharacterInvolved(character).storyEvent

                val update = scene.withStoryEvent(storyEvent)

                return listOf(dynamicTest("character should have been included") {
                    update.scene.includedCharacters.getOrError(character.id).run {
                        sceneId.mustEqual(scene.id)
                        characterName.mustEqual(character.name)
                    }
                }, dynamicTest("should produce story event added event") {
                    update as Successful
                    update.event.storyEventId.mustEqual(storyEvent.id)
                    update.event.sceneId.mustEqual(scene.id)
                    update.event.storyEventName.mustEqual(storyEvent.name)
                }, dynamicTest("should produce character added event") {
                    update as Successful
                    update.event.characterInSceneUpdates.mustEqual(
                        listOf(
                            IncludedCharacterInScene(
                                scene.id, Scene.IncludedCharacter(character.id, character.name.value)
                            )
                        )
                    )
                })
            }

            @TestFactory
            fun `Given Scene Already Includes Character Involved with Added Story Event`(): List<DynamicNode> {
                val storyEvent = storyEvent.withCharacterInvolved(character).storyEvent
                val otherStoryEvent = makeStoryEvent()
                val scene = scene.withStoryEvent(otherStoryEvent).scene.withCharacterIncluded(
                    CharacterInvolvedInStoryEvent(
                        otherStoryEvent.id, character.id, character.name.value
                    )
                ).scene

                val update = scene.withStoryEvent(storyEvent)

                return listOf(dynamicTest("should produce story event added event") {
                    update as Successful
                    update.event.storyEventId.mustEqual(storyEvent.id)
                    update.event.sceneId.mustEqual(scene.id)
                    update.event.storyEventName.mustEqual(storyEvent.name)
                }, dynamicTest("should not produce character added event") {
                    update as Successful
                    update.event.characterInSceneUpdates.filterIsInstance<IncludedCharacterInScene>().isEmpty()
                        .mustEqual(true)
                }, dynamicTest("should produce character source added event") {
                    update as Successful
                    update.event.characterInSceneUpdates.mustEqual(
                        listOf(
                            CharacterInSceneSourceAdded(
                                scene.id, character.id, storyEvent.id
                            )
                        )
                    )
                })
            }

        }

        private fun `character should not be included`(sceneUpdate: SceneUpdate<*>) =
            dynamicTest("character should not have been included") {
                assertFalse(sceneUpdate.scene.includesCharacter(character.id))
            }

        private fun `no event should have been produced`(sceneUpdate: SceneUpdate<*>) =
            dynamicTest("update should be unsuccessful") {
                sceneUpdate as UnSuccessful
                sceneUpdate.reason
            }

    }

    @Nested
    inner class `Remove Character from Scene` {

        @Nested
        inner class `Via Response to Character Removed from Covered Story Event` {

            @Test
            fun `Given Story Event is not Covered`() {
                @Suppress("DEPRECATION") val update = scene
                    .withStoryEvent(makeStoryEvent().withCharacterInvolved(character).storyEvent)
                    .scene.withCharacter(character.id)?.withoutSource(storyEvent.id)

                update as UnSuccessful
                update.reason.mustEqual(SceneDoesNotCoverStoryEvent(scene.id, storyEvent.id))
            }

            @Test
            fun `Given Story Event has been Covered`() {
                @Suppress("DEPRECATION") val update = scene
                    .withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                    .scene.withCharacter(character.id)?.withoutSource(storyEvent.id)

                update as Successful
                update.scene.includedCharacters.get(character.id).mustEqual(null)
                update.scene.includesCharacter(character.id).mustEqual(false)

                update.event.mustEqual(CharacterRemovedFromScene(scene.id, character.id))
            }

            @Test
            fun `Given Another Covered Story Event Still Involves Character`() {
                val otherStoryEvent = makeStoryEvent().withCharacterInvolved(character).storyEvent
                val scene = scene.withStoryEvent(otherStoryEvent).scene.withStoryEvent(
                    storyEvent.withCharacterInvolved(character).storyEvent
                ).scene

                @Suppress("DEPRECATION")
                val update = scene.withCharacter(character.id)?.withoutSource(storyEvent.id)

                update as Successful
                update.scene.includedCharacters.getOrError(character.id)
                update.scene.includesCharacter(character.id).mustEqual(true)

                update.event.mustEqual(CharacterInSceneSourceRemoved(scene.id, character.id, storyEvent.id))
            }

        }

        @Nested
        inner class `Via Story Event Uncovered` {

            @Test
            fun `Uncover a Story Event that doesn't Involve Character`() {
                val storyEventToUncover = makeStoryEvent()
                val scene = scene.withStoryEvent(storyEventToUncover)
                    .scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                    .scene

                val update = scene.withoutStoryEvent(storyEventToUncover.id)

                update as Successful
                update.event.updatedCharacters.isEmpty().mustEqual(true)

                update.scene.includesCharacter(character.id).mustEqual(true)
            }

            @Test
            fun `Uncover last Story Event that Involves Character`() {
                val scene = scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                    .scene

                val update = scene.withoutStoryEvent(storyEvent.id)

                update as Successful
                update.event.updatedCharacters.mustEqual(listOf(CharacterRemovedFromScene(scene.id, character.id)))

                update.scene.includesCharacter(character.id).mustEqual(false)
            }

            @Test
            fun `Uncover Story Event that Involves Character, but other Story Events Still Involve Character`() {
                val storyEventToUncover = makeStoryEvent().withCharacterInvolved(character).storyEvent
                val scene = scene.withStoryEvent(storyEventToUncover)
                    .scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                    .scene

                val update = scene.withoutStoryEvent(storyEventToUncover.id)

                update as Successful
                update.event.updatedCharacters.mustEqual(
                    listOf(
                        CharacterInSceneSourceRemoved(
                            scene.id,
                            character.id,
                            storyEventToUncover.id
                        )
                    )
                )

                update.scene.includesCharacter(character.id).mustEqual(true)
            }

        }

    }

    @Nested
    inner class `Rename Character in Scene` {

        @Test
        fun `cannot rename character that is not in the scene`() {
            scene.withCharacter(character.id)?.renamed("New Name").mustEqual(null)
        }

        @Test
        fun `no update should be produced if the name is identical`() {
            val update = scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                .scene.withCharacter(character.id)?.renamed(character.name.value)

            update as UnSuccessful
            update.scene.includedCharacters.getOrError(character.id).characterName.mustEqual(character.name.value)

            update.reason.mustEqual(characterInSceneAlreadyHasName(scene.id, character.id, character.name.value))
        }

        @Test
        fun `should update scene when name is new`() {
            val newName = "New Name"
            val update = scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
                .scene.withCharacter(character.id)?.renamed(newName)

            update as Successful
            update.scene.includedCharacters.getOrError(character.id).characterName.mustEqual(newName)

            update.event.mustEqual(CharacterInSceneRenamed(scene.id, character.id, newName))
        }
    }

    @Nested
    inner class `Assign Role to Character in Scene` {

        private val sceneWithCharacter = scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
            .scene

        @Test
        fun `cannot update character's role if they aren't in the scene`() {
            assertNull(scene.withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter))
        }

        @Test
        fun `a character should not have a role initially`() {
            val defaultRole = sceneWithCharacter
                .includedCharacters.getOrError(character.id)
                .roleInScene

            assertNull(defaultRole)
        }

        @Test
        fun `can assign a new role`() {
            val newRole = RoleInScene.IncitingCharacter
            val roleUpdate = sceneWithCharacter
                .withCharacter(character.id)?.assignedRole(newRole)

            roleUpdate as Successful
            roleUpdate.scene.includedCharacters.getOrError(character.id).roleInScene.mustEqual(newRole)

            roleUpdate.event.events.single().mustEqual(CharacterAssignedRoleInScene(scene.id, character.id, newRole))
        }

        @Test
        fun `can clear a character's role`() {
            val roleUpdate = sceneWithCharacter
                .withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter)
                ?.scene?.withCharacter(character.id)?.assignedRole(null)

            roleUpdate as Successful
            assertNull(roleUpdate.scene.includedCharacters.getOrError(character.id).roleInScene)

            roleUpdate.event.events.single().mustEqual(CharacterRoleInSceneCleared(scene.id, character.id))
        }

        @Test
        fun `assigning the same role should not produce an update`() {
            listOf(
                sceneWithCharacter.withCharacter(character.id)?.assignedRole(null),

                sceneWithCharacter.withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter)
                    ?.scene?.withCharacter(character.id)?.assignedRole(RoleInScene.IncitingCharacter),

                sceneWithCharacter.withCharacter(character.id)?.assignedRole(RoleInScene.OpponentCharacter)
                    ?.scene?.withCharacter(character.id)?.assignedRole(RoleInScene.OpponentCharacter)
            ).forEachIndexed { index, roleUpdate ->
                if (roleUpdate !is UnSuccessful) {
                    error("Should not have received update for $index test")
                }
            }
        }

        @Nested
        inner class `Assign Second Inciting Character` {

            val secondCharacter = makeCharacter()
            val sceneWithIncitingCharacter = sceneWithCharacter
                .withCharacterIncluded(
                    CharacterInvolvedInStoryEvent(
                        storyEvent.id,
                        secondCharacter.id,
                        secondCharacter.name.value
                    )
                )
                .scene.withCharacter(character.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                .scene

            @Test
            fun `should clear previous inciting character's role`() {
                val sceneUpdate = sceneWithIncitingCharacter
                    .withCharacter(secondCharacter.id)!!.assignedRole(RoleInScene.IncitingCharacter)

                assertNull(sceneUpdate.scene.includedCharacters.getOrError(character.id).roleInScene)
            }

            @Test
            fun `should output role cleared event and role assignment event`() {
                val sceneUpdate = sceneWithIncitingCharacter
                    .withCharacter(secondCharacter.id)!!.assignedRole(RoleInScene.IncitingCharacter)

                sceneUpdate as Successful
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
    inner class `Change Character Design in Scene` {

        private val sceneWithCharacter = scene.withStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
            .scene

        @Test
        fun `characters have no desire when first included`() {
            sceneWithCharacter
                .includedCharacters.getOrError(character.id)
                .desire.mustEqual("")
        }

        @Test
        fun `cannot update character desire if character is not included`() {
            assertNull(scene.withCharacter(character.id)?.desireChanged("New Desire"))
        }

        @Test
        fun `can update included character's desire`() {
            val update = sceneWithCharacter
                .withCharacter(character.id)!!.desireChanged("New Desire")

            update as Successful
            update.event.mustEqual(CharacterDesireInSceneChanged(scene.id, character.id, "New Desire"))
            update.scene.includedCharacters.getOrError(character.id).desire.mustEqual("New Desire")
        }

        @Test
        fun `passing in same desire should return no update`() {
            val update = sceneWithCharacter
                .withCharacter(character.id)!!.desireChanged("New Desire").scene
                .withCharacter(character.id)!!.desireChanged("New Desire")

            update as UnSuccessful
            update.reason.mustEqual(characterInSceneAlreadyHasDesire(scene.id, character.id, "New Desire"))
        }

    }
}