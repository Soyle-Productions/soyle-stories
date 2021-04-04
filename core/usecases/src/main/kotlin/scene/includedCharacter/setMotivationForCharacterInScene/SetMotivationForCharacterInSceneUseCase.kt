package com.soyle.stories.usecase.scene.includedCharacter.setMotivationForCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class SetMotivationForCharacterInSceneUseCase(
  private val sceneRepository: SceneRepository,
  private val characterRepository: CharacterRepository
) : SetMotivationForCharacterInScene {

	override suspend fun invoke(request: SetMotivationForCharacterInScene.RequestModel, output: SetMotivationForCharacterInScene.OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToSetMotivationForCharacterInScene(e) }
		output.motivationSetForCharacterInScene(response)
	}

	private suspend fun execute(request: SetMotivationForCharacterInScene.RequestModel): SetMotivationForCharacterInScene.ResponseModel {
		val scene = getScene(request)
		val character = getCharacter(request.characterId)
		setMotivationIfNeeded(scene, character, request)
		return convertRequestToResponse(request)
	}

	private suspend fun getScene(request: SetMotivationForCharacterInScene.RequestModel) =
	  (sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId))

	private suspend fun getCharacter(characterId: UUID) =
	  (characterRepository.getCharacterById(Character.Id(characterId))
		?: throw CharacterDoesNotExist(characterId))

	private suspend fun setMotivationIfNeeded(scene: Scene, character: Character, request: SetMotivationForCharacterInScene.RequestModel) {
		if (scene.getMotivationForCharacter(character.id)?.motivation != request.motivation) {
			sceneRepository.updateScene(scene.withMotivationForCharacter(character.id, request.motivation))
		}
	}

	private fun convertRequestToResponse(request: SetMotivationForCharacterInScene.RequestModel): SetMotivationForCharacterInScene.ResponseModel {
		return SetMotivationForCharacterInScene.ResponseModel(
		  request.sceneId,
		  request.characterId,
		  request.motivation
		)
	}


}