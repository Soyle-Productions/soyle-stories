package com.soyle.stories.usecase.scene.includedCharacter

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneCleared
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotIncludeCharacter
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.includedCharacter.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.includedCharacter.assignRole.AssignRoleToCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Assign Role to Character in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    private var updatedScene: Scene? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    @Test
    fun `scene does not exist`() {
        assertThrowsSceneDoesNotExist(scene.id) {
            assignRoleToCharacterInScene()
        }
        assertNull(updatedScene)
    }

    @Test
    fun `same role should not update scene or produce event`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
        val result = assignRoleToCharacterInScene()
        assertNull(result.characterRoleInSceneChanged)
        assertNull(updatedScene)
    }

    @Test
    fun `assigning role should output event and update scene`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
        val result = assignRoleToCharacterInScene(RoleInScene.IncitingCharacter)
        result.characterRoleInSceneChanged as CharacterAssignedRoleInScene
        updatedScene!!.includedCharacters.getOrError(character.id).roleInScene.mustEqual(RoleInScene.IncitingCharacter)
    }

    @Test
    fun `clearing role should output event and update scene`() {
        sceneRepository.givenScene(
            scene.withCharacterIncluded(character).scene.withRoleForCharacter(
                character.id,
                RoleInScene.OpponentCharacter
            ).scene
        )
        val result = assignRoleToCharacterInScene()
        result.characterRoleInSceneChanged as CharacterRoleInSceneCleared
        assertNull(updatedScene!!.includedCharacters.getOrError(character.id).roleInScene)
    }

    private fun assignRoleToCharacterInScene(roleInScene: RoleInScene? = null): AssignRoleToCharacterInScene.ResponseModel =
        runBlocking {
            val useCase: AssignRoleToCharacterInScene = AssignRoleToCharacterInSceneUseCase(sceneRepository)
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