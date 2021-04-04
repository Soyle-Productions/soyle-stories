package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class `Listed Characters Can be Included Int Test` {

    private val scene = makeScene()
    private val character = makeCharacter(projectId = scene.projectId)

    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    init {
        sceneRepository.givenScene(scene)
        storyEventRepository.givenStoryEvent(makeStoryEvent(id = scene.storyEventId))
        characterRepository.givenCharacter(character)
        repeat(4) { characterRepository.givenCharacter(makeCharacter(projectId = scene.projectId)) }
    }

    @Test
    fun `can include a listed character`() {
        val availableCharacters = listAvailableCharctersToIncludeInScene()
        includeCharacterInScene(Character.Id(availableCharacters.first().characterId))
    }

    @Test
    fun `included character should not be listed`() {
        includeCharacterInScene(character.id)
        val availableCharacters = listAvailableCharctersToIncludeInScene()
        assertNull(availableCharacters.find { it.characterId == character.id.uuid })
    }

    private fun listAvailableCharctersToIncludeInScene(): AvailableCharactersToAddToScene
    {
        val useCase: ListAvailableCharactersToIncludeInScene = IncludeCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        var result: AvailableCharactersToAddToScene? = null
        val output = object : ListAvailableCharactersToIncludeInScene.OutputPort {
            override suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id.uuid, output)
        }
        return result!!
    }

    private fun includeCharacterInScene(characterId: Character.Id): IncludeCharacterInScene.ResponseModel
    {
        val useCase: IncludeCharacterInScene = IncludeCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        var result: IncludeCharacterInScene.ResponseModel? = null
        val output = object : IncludeCharacterInScene.OutputPort {
            override suspend fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id.uuid, characterId.uuid, output)
        }
        return result!!
    }

}