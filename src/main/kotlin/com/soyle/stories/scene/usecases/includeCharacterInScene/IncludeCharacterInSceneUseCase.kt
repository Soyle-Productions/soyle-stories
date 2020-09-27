package com.soyle.stories.scene.usecases.includeCharacterInScene

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.getCharacterOrError
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.NoSceneExistsWithStoryEventId
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.scene.usecases.common.*
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

class IncludeCharacterInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository
) : IncludeCharacterInScene, GetAvailableCharactersToAddToScene {

    override suspend fun invoke(sceneId: UUID, output: GetAvailableCharactersToAddToScene.OutputPort) {
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

        val updatedScene = scene.withCharacterIncluded(character)
        val updatedStoryEvent = storyEvent.withIncludedCharacterId(character.id)

        storyEventRepository.updateStoryEvent(updatedStoryEvent)
        sceneRepository.updateScene(updatedScene)

        val responseModel = IncludeCharacterInScene.ResponseModel(
            getCharacterDetails(updatedScene, character),
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
            character.name,
            null,
            getInheritedMotivation(scene, character)
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
            sceneRepository.updateScene(scene.withCharacterIncluded(character))
        }
    }

    private suspend fun getCharacter(response: AddCharacterToStoryEvent.ResponseModel) =
        (characterRepository.getCharacterById(Character.Id(response.characterId))
            ?: throw CharacterDoesNotExist(response.characterId))

    private suspend fun getScene(response: AddCharacterToStoryEvent.ResponseModel) =
        (sceneRepository.getSceneForStoryEvent(StoryEvent.Id(response.storyEventId))
            ?: throw NoSceneExistsWithStoryEventId(response.storyEventId))

}