package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneCleared
import com.soyle.stories.domain.scene.character.exceptions.CharacterAlreadyDoesNotHaveRoleInScene
import com.soyle.stories.domain.scene.character.exceptions.CharacterAlreadyHasRoleInScene
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Assign Role to Character in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    private var updatedScene: Scene? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble(initialCharacters = listOf(character))

    @Test
    fun `given scene does not exist - should throw error`() {
        val result = kotlin.runCatching { assignRoleToCharacterInScene() }

        result.exceptionOrNull().shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        assertNull(updatedScene)
    }

    @Nested
    inner class `Character Must Exist` {

        init {
            sceneRepository.givenScene(scene)
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `given character does not exist - should throw error`() {
            val result = kotlin.runCatching { assignRoleToCharacterInScene() }

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }

    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `given scene does not include character - should throw error`() {
            val result = kotlin.runCatching { assignRoleToCharacterInScene() }

            result.exceptionOrNull().shouldBeEqualTo(SceneDoesNotIncludeCharacter(scene.id, character.id))
            assertNull(updatedScene)
        }

        @Test
        fun `character not included, but covered story event involves character`() {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(InvolvedCharacter(character.id, "")))
                .also(storyEventRepository::givenStoryEvent)

            val result = kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }

            val response = result.getOrThrow()
            response.characterIncludedInScene.shouldBeEqualTo(CharacterIncludedInScene(scene.id, character.id, character.displayName.value, scene.projectId))
            response.characterRolesInSceneChanged.shouldBeEqualTo(CompoundEvent(
                CharacterAssignedRoleInScene(scene.id, character.id, RoleInScene.IncitingCharacter)
            ))
        }

        @Nested
        inner class `Given Scene Includes Character` {

            init {
                sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
            }

            @Test
            fun `should output event`() {
                val result = kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }

                val event = result.getOrNull()!!
                event.characterRolesInSceneChanged.shouldBeEqualTo(CompoundEvent(
                    CharacterAssignedRoleInScene(scene.id, character.id, RoleInScene.IncitingCharacter)
                ))
            }

            @Test
            fun `should update scene`() {
                val result = kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }

                updatedScene!!
            }

            @Test
            fun `duplicate calls should throw error`() {
                kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }
                updatedScene = null

                val result = kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }

                val error = result.exceptionOrNull()
                error.shouldBeEqualTo(CharacterAlreadyHasRoleInScene(scene.id, character.id, RoleInScene.IncitingCharacter))
                assertNull(updatedScene)
            }

            @Test
            fun `clearing the role should throw error`() {
                val result = kotlin.runCatching { assignRoleToCharacterInScene(null) }

                val error = result.exceptionOrNull()
                error.shouldBeEqualTo(CharacterAlreadyDoesNotHaveRoleInScene(scene.id, character.id))
                assertNull(updatedScene)
            }

            @Nested
            inner class `Given Character Has a Role In Scene` {

                init {
                    scene.withCharacterIncluded(character)
                        .scene.withCharacter(character.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                        .scene.let(sceneRepository::givenScene)
                }

                @Test
                fun `clearing the role should produce an update`() {
                    val result = kotlin.runCatching { assignRoleToCharacterInScene(null) }

                    val event = result.getOrNull()!!
                    event.characterRolesInSceneChanged.shouldBeEqualTo(CompoundEvent(
                        CharacterRoleInSceneCleared(scene.id, character.id)
                    ))
                }

                @Test
                fun `clearing the role should update scene`() {
                    val result = kotlin.runCatching { assignRoleToCharacterInScene(null) }

                    updatedScene!!
                }

                @Test
                fun `clearing the role twice should throw error`() {
                    kotlin.runCatching { assignRoleToCharacterInScene(null) }
                    updatedScene = null

                    val result = kotlin.runCatching { assignRoleToCharacterInScene(null) }

                    val error = result.exceptionOrNull()
                    error.shouldBeEqualTo(CharacterAlreadyDoesNotHaveRoleInScene(scene.id, character.id))
                    assertNull(updatedScene)
                }

            }

            @Nested
            inner class `Given Another Character is Already Inciting Character` {

                private val secondCharacter = makeCharacter()
                init {
                    scene.withCharacterIncluded(secondCharacter)
                        .scene.withCharacter(secondCharacter.id)!!.assignedRole(RoleInScene.IncitingCharacter)
                        .scene.withCharacterIncluded(character)
                        .scene.let(sceneRepository::givenScene)
                }

                @Test
                fun `assigning inciting role should produce two events`() {
                    val result = kotlin.runCatching { assignRoleToCharacterInScene(RoleInScene.IncitingCharacter) }

                    val event = result.getOrNull()!!
                    event.characterRolesInSceneChanged.shouldBeEqualTo(CompoundEvent(
                        CharacterRoleInSceneCleared(scene.id, secondCharacter.id),
                        CharacterAssignedRoleInScene(scene.id, character.id, RoleInScene.IncitingCharacter),
                    ))
                    updatedScene!!
                }

            }

        }

    }

    private fun assignRoleToCharacterInScene(roleInScene: RoleInScene? = null): AssignRoleToCharacterInScene.ResponseModel =
        runBlocking {
            val useCase: AssignRoleToCharacterInScene = AssignRoleToCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
            val output = object : AssignRoleToCharacterInScene.OutputPort {
                lateinit var result: AssignRoleToCharacterInScene.ResponseModel
                override suspend fun roleAssignedToCharacterInScene(response: AssignRoleToCharacterInScene.ResponseModel) {
                    result = response
                }
            }
            val request = AssignRoleToCharacterInScene.RequestModel(scene.id, character.id, roleInScene)
            useCase.invoke(request, output)
            output.result
        }

}