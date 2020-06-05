package com.soyle.stories.scene.usecases.includeCharacterInScene

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.NoSceneExistsWithStoryEventId
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent

class IncludeCharacterInSceneUseCase(
  private val sceneRepository: SceneRepository,
  private val characterRepository: CharacterRepository
) : IncludeCharacterInScene {

	override suspend fun invoke(response: AddCharacterToStoryEvent.ResponseModel, outputPort: IncludeCharacterInScene.OutputPort) {
		val responseModel = try { execute(response) }
		catch (e: Exception) { return outputPort.failedToIncludeCharacterInScene(e) }
		outputPort.characterIncludedInScene(responseModel)
	}

	private suspend fun execute(response: AddCharacterToStoryEvent.ResponseModel): IncludeCharacterInScene.ResponseModel {
		val scene = getScene(response)
		val character = getCharacter(response)
		addCharacterIfNotIncluded(scene, character)
		return IncludeCharacterInScene.ResponseModel(scene.id.uuid, character.id.uuid)
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