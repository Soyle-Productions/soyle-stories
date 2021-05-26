package com.soyle.stories.usecase.scene.renameScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class RenameSceneUseCase(
  private val sceneRepository: SceneRepository
) : RenameScene {
	override suspend fun invoke(request: RenameScene.RequestModel, output: RenameScene.OutputPort) {
		val response = try {
			renameScene(request)
		} catch (s: Exception) {
			return output.receiveRenameSceneFailure(s)
		}
		output.receiveRenameSceneResponse(response)
	}

	private suspend fun renameScene(request: RenameScene.RequestModel): RenameScene.ResponseModel {
		val scene = getScene(request.sceneId, request.locale)
		updateSceneIfNeeded(scene, request.name)
		return RenameScene.ResponseModel(scene.id.uuid, request.name.value)
	}

	private suspend fun getScene(sceneId: UUID, locale: SceneLocale): Scene {
		return sceneRepository.getSceneById(Scene.Id(sceneId))
		  ?: throw SceneDoesNotExist(locale, sceneId)
	}

	private suspend fun updateSceneIfNeeded(scene: Scene, newName: NonBlankString)
	{
		if (scene.name != newName) {
			sceneRepository.updateScene(scene.withName(newName).scene)
		}
	}
}