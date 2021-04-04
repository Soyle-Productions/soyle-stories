package com.soyle.stories.usecase.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.*
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.scene.NoSceneExistsWithStoryEventId
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

class IncludeCharacterInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository
) : IncludeCharacterInScene, ListAvailableCharactersToIncludeInScene {

    override suspend fun invoke(sceneId: UUID, output: ListAvailableCharactersToIncludeInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId)
        val characters = characterRepository.listCharactersInProject(scene.projectId)
        val availableCharacters = characters.filterNot { scene.includesCharacter(it.id) }.map { CharacterItem(it) }
        val response = AvailableCharactersToAddToScene(scene.id.uuid, availableCharacters)
        output.receiveAvailableCharactersToAddToScene(response)
    }

    override suspend fun invoke(sceneId: UUID, characterId: UUID, outputPort: IncludeCharacterInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId)
        val storyEvent = storyEventRepository.getStoryEventById(scene.storyEventId)!!
        val character = characterRepository.getCharacterOrError(characterId)

        val sceneUpdate = scene.withCharacterIncluded(character)
        val updatedStoryEvent = storyEvent.withIncludedCharacterId(character.id)

        if (sceneUpdate is Updated) {
            storyEventRepository.updateStoryEvent(updatedStoryEvent)
            sceneRepository.updateScene(sceneUpdate.scene)
        }

        val responseModel = IncludeCharacterInScene.ResponseModel(
            getCharacterDetails(sceneUpdate.scene, character),
            IncludedCharacterInStoryEvent(storyEvent.id.uuid, character.id.uuid)
        )
        outputPort.characterIncludedInScene(responseModel)
    }

    override suspend fun invoke(
        response: AddCharacterToStoryEvent.ResponseModel,
        outputPort: IncludeCharacterInScene.OutputPort
    ) {
        val scene = getScene(response)
        val character = getCharacter(response)
        addCharacterIfNotIncluded(scene, character)
        val responseModel = IncludeCharacterInScene.ResponseModel(
            getCharacterDetails(scene, character),
            null
        )
        outputPort.characterIncludedInScene(responseModel)
    }

    private suspend fun getCharacterDetails(scene: Scene, character: Character): IncludedCharacterInScene {
        return IncludedCharacterInScene(
            scene.id.uuid,
            character.id.uuid,
            character.name.value,
            null,
            getInheritedMotivation(scene, character),
            listOf()
        )
    }

    private suspend fun getInheritedMotivation(scene: Scene, character: Character): InheritedMotivation? {
        return getScenesBefore(scene, sceneRepository)
            .sortedByProjectOrder(scene.projectId, sceneRepository)
            .asReversed()
            .let {
                getLastSetMotivation(it, character.id)
            }
    }

    private suspend fun addCharacterIfNotIncluded(scene: Scene, character: Character) {
        if (!scene.includesCharacter(character.id)) {
            sceneRepository.updateScene(scene.withCharacterIncluded(character).scene)
        }
    }

    private suspend fun getCharacter(response: AddCharacterToStoryEvent.ResponseModel) =
        (characterRepository.getCharacterById(Character.Id(response.characterId))
            ?: throw CharacterDoesNotExist(response.characterId))

    private suspend fun getScene(response: AddCharacterToStoryEvent.ResponseModel) =
        (sceneRepository.getSceneForStoryEvent(StoryEvent.Id(response.storyEventId))
            ?: throw NoSceneExistsWithStoryEventId(response.storyEventId))

}