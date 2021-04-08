package com.soyle.stories.usecase.scene.character.includeCharacterInScene

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
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

class IncludeCharacterInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository
) : IncludeCharacterInScene, ListAvailableCharactersToIncludeInScene {

    /**
     * List Available Characters to Include in Scene
     */
    override suspend fun invoke(sceneId: UUID, output: ListAvailableCharactersToIncludeInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId)
        val characters = characterRepository.listCharactersInProject(scene.projectId)
        val availableCharacters = characters.filterNot { scene.includesCharacter(it.id) }.map { CharacterItem(it) }
        val response = AvailableCharactersToAddToScene(scene.id.uuid, availableCharacters)
        output.receiveAvailableCharactersToAddToScene(response)
    }

    /**
     * Include Character in Scene
     */
    override suspend fun invoke(sceneId: UUID, characterId: UUID, outputPort: IncludeCharacterInScene.OutputPort) {
        // get entities from repositories
        val scene = sceneRepository.getSceneOrError(sceneId)
        val storyEvent = storyEventRepository.getStoryEventById(scene.storyEventId)!!
        val character = characterRepository.getCharacterOrError(characterId)

        // update scene and backing story event
        val sceneUpdate = scene.withCharacterIncluded(character)
        val updatedStoryEvent = storyEvent.withIncludedCharacterId(character.id)

        // persist scene and backing story event, if updated successfully
        if (sceneUpdate is Updated) {
            storyEventRepository.updateStoryEvent(updatedStoryEvent)
            sceneRepository.updateScene(sceneUpdate.scene)
        }

        val responseModel = IncludeCharacterInScene.ResponseModel(
            sceneUpdate.scene.id,
            getCharacterDetails(sceneUpdate.scene, character),
            IncludedCharacterInStoryEvent(storyEvent.id.uuid, character.id.uuid)
        )
        outputPort.characterIncludedInScene(responseModel)
    }

    /**
     * Include Character in Scene After Included in Story Event
     */
    override suspend fun invoke(
        response: AddCharacterToStoryEvent.ResponseModel,
        outputPort: IncludeCharacterInScene.OutputPort
    ) {
        val scene = getScene(response)
        val character = getCharacter(response)
        addCharacterIfNotIncluded(scene, character)
        val responseModel = IncludeCharacterInScene.ResponseModel(
            scene.id,
            getCharacterDetails(scene, character),
            null
        )
        outputPort.characterIncludedInScene(responseModel)
    }

    private suspend fun getCharacterDetails(scene: Scene, character: Character): IncludedCharacterInScene {
        val previousMotivations = PreviousMotivations(scene, sceneRepository)
        return IncludedCharacterInScene(
            scene.id,
            character.id,
            character.name.value,
            null,
            null,
            previousMotivations.getLastSetMotivation(character.id),
            listOf()
        )
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