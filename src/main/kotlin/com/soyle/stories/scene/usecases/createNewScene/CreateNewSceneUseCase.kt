package com.soyle.stories.scene.usecases.createNewScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.SceneNameCannotBeBlank
import com.soyle.stories.scene.repositories.SceneRepository

class CreateNewSceneUseCase(
  private val sceneRepository: SceneRepository
) : CreateNewScene {

	override suspend fun invoke(name: String, locale: Locale, output: CreateNewScene.OutputPort) {
		val response = try {
			createNewScene(name, locale)
		} catch (e: Exception) {
			if (e is SceneException) {
				return output.receiveCreateNewSceneFailure(e)
			} else throw e
		}
		output.receiveCreateNewSceneResponse(response)
	}

	private suspend fun createNewScene(name: String, locale: Locale): CreateNewScene.ResponseModel {
		validateSceneName(name, locale)
		val scene = Scene(Scene.Id(), name)
		sceneRepository.createNewScene(scene)
		return CreateNewScene.ResponseModel(scene.id.uuid, name)
	}

	private fun validateSceneName(name: String, locale: Locale) {
		if (name.isBlank()) throw SceneNameCannotBeBlank(locale)
	}

}