package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneCleared
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

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
        assertNull(result.characterRolesInSceneChanged)
        assertNull(updatedScene)
    }

    @Test
    fun `assigning role should output event and update scene`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
        val result = assignRoleToCharacterInScene(RoleInScene.IncitingCharacter)
        result.characterRolesInSceneChanged?.events?.single() as CharacterAssignedRoleInScene
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
        result.characterRolesInSceneChanged?.events?.single() as CharacterRoleInSceneCleared
        assertNull(updatedScene!!.includedCharacters.getOrError(character.id).roleInScene)
    }

    @Test
    fun `all events should be output`() {
        val secondCharacter = makeCharacter()
        sceneRepository.givenScene(
            scene.withCharacterIncluded(character).scene
                .withCharacterIncluded(secondCharacter).scene
                .withRoleForCharacter(secondCharacter.id, RoleInScene.IncitingCharacter).scene
        )
        val result = assignRoleToCharacterInScene(RoleInScene.IncitingCharacter)
        result.characterRolesInSceneChanged?.events?.component1()  as CharacterRoleInSceneCleared
        result.characterRolesInSceneChanged?.events?.component2()  as CharacterAssignedRoleInScene
        assertNull(updatedScene!!.includedCharacters.getOrError(secondCharacter.id).roleInScene)
        updatedScene!!.includedCharacters.getOrError(character.id).roleInScene.mustEqual(RoleInScene.IncitingCharacter)
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