package com.soyle.stories.usecase.scene.deleteScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class DeleteSceneUseCase(
  private val sceneRepository: SceneRepository
) : DeleteScene {
	override suspend fun invoke(sceneId: UUID, locale: SceneLocale, output: DeleteScene.OutputPort) {
		val response = try {
			val scene = sceneRepository.getSceneById(Scene.Id(sceneId)) ?: throw SceneDoesNotExist(locale, sceneId)
			sceneRepository.removeScene(scene)
			DeleteScene.ResponseModel(sceneId)
		} catch (s: Exception) {
			return output.receiveDeleteSceneFailure(s)
		}
		output.receiveDeleteSceneResponse(response)
	}
}