package com.soyle.stories.scene.usecases.deleteScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.repositories.SceneRepository
import java.util.*

class DeleteSceneUseCase(
  private val sceneRepository: SceneRepository
) : DeleteScene {
	override suspend fun invoke(sceneId: UUID, locale: Locale, output: DeleteScene.OutputPort) {
		val response = try {
			val scene = sceneRepository.getSceneById(Scene.Id(sceneId)) ?: throw SceneDoesNotExist(locale, sceneId)
			sceneRepository.removeScene(scene)
			DeleteScene.ResponseModel(sceneId)
		} catch (s: SceneException) {
			return output.receiveDeleteSceneFailure(s)
		}
		output.receiveDeleteSceneResponse(response)
	}
}