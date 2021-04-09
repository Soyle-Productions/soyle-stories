package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.CharacterAssignedRoleInScene
import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneCleared
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class `Set Character Desire in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    private var updatedScene: Scene? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    @Test
    fun `scene does not exist`() {
        assertThrowsSceneDoesNotExist(scene.id) {
            setCharacterDesireInScene("")
        }
        Assertions.assertNull(updatedScene)
    }

    @Test
    fun `assigning desire should output event and update scene`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
        val result = setCharacterDesireInScene("Get dat bread")
        result.characterDesireInSceneChanged!!
        updatedScene!!.includedCharacters.getOrError(character.id).desire.mustEqual("Get dat bread")
    }

    @Test
    fun `same desire should not update scene or produce event`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene.withDesireForCharacter(character.id, "Get dat bread").scene)
        val result = setCharacterDesireInScene("Get dat bread")
        Assertions.assertNull(result.characterDesireInSceneChanged)
        Assertions.assertNull(updatedScene)
    }

    private fun setCharacterDesireInScene(desire: String): SetCharacterDesireInScene.ResponseModel =
        runBlocking {
            val useCase: SetCharacterDesireInScene = SetCharacterDesireInSceneUseCase(sceneRepository)
            val output = object : SetCharacterDesireInScene.OutputPort {
                lateinit var result: SetCharacterDesireInScene.ResponseModel
                override suspend fun receiveSetCharacterDesireInSceneResponse(response: SetCharacterDesireInScene.ResponseModel) {
                    result = response
                }
            }
            val request = SetCharacterDesireInScene.RequestModel(scene.id, character.id, desire)
            useCase.invoke(request, output)
            output.result
        }

}