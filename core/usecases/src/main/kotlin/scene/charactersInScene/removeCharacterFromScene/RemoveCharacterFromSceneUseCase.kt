package com.soyle.stories.usecase.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository

class RemoveCharacterFromSceneUseCase(
  private val sceneRepository: SceneRepository
) : RemoveCharacterFromScene {

	override suspend fun invoke(request: RemoveCharacterFromScene.RequestModel, output: RemoveCharacterFromScene.OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToRemoveCharacterFromScene(e) }
		if (response != null) output.characterRemovedFromScene(response)
	}

	private suspend fun execute(request: RemoveCharacterFromScene.RequestModel): RemoveCharacterFromScene.ResponseModel?
	{
		val scene = getScene(request) ?: return null
		val characterId = Character.Id(request.characterId)
		if (! scene.includesCharacter(characterId)) {
			return characterNotInScene(scene, request)
		}
		sceneRepository.updateScene(scene.withoutCharacter(characterId))
		return RemoveCharacterFromScene.ResponseModel(scene.id.uuid, request.characterId)
	}

	private suspend fun getScene(request: RemoveCharacterFromScene.RequestModel): Scene?
	{
		return if (request.sceneId != null) {
			(sceneRepository.getSceneById(Scene.Id(request.sceneId))
			  ?: throw SceneDoesNotExist(request.locale, request.sceneId))
		} else {
			sceneRepository.getSceneForStoryEvent(StoryEvent.Id(request.storyEventId!!))
		}
	}

	private fun characterNotInScene(scene: Scene, request: RemoveCharacterFromScene.RequestModel): Nothing?
	{
		if (request.storyEventId == null) {
			throw SceneDoesNotIncludeCharacter(scene.id, Character.Id(request.characterId))
		}
		return null
	}
}