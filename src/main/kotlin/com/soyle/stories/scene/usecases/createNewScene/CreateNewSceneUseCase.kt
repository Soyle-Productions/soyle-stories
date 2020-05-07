package com.soyle.stories.scene.usecases.createNewScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.SceneNameCannotBeBlank
import com.soyle.stories.scene.repositories.SceneRepository

class CreateNewSceneUseCase(
  private val sceneRepository: SceneRepository
) : CreateNewScene {

	override suspend fun invoke(name: String, output: CreateNewScene.OutputPort) {
		val response = try {
			createNewScene(name)
		} catch (s: SceneException) {
			return output.receiveCreateNewSceneFailure(s)
		}
		output.receiveCreateNewSceneResponse(response)
	}

	private suspend fun createNewScene(name: String): CreateNewScene.ResponseModel {
		validateSceneName(name)
		val scene = Scene(Scene.Id(), name)
		sceneRepository.createNewScene(scene)
		return CreateNewScene.ResponseModel(scene.id.uuid, name)
	}

	private fun validateSceneName(name: String)
	{
		if (name.isBlank()) throw SceneNameCannotBeBlank
	}

}