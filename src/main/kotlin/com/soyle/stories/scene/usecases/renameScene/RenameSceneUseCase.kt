package com.soyle.stories.scene.usecases.renameScene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.validateSceneName
import java.util.*

class RenameSceneUseCase(
  private val sceneRepository: SceneRepository
) : RenameScene {
	override suspend fun invoke(request: RenameScene.RequestModel, output: RenameScene.OutputPort) {
		val response = try {
			renameScene(request)
		} catch (s: SceneException) {
			return output.receiveRenameSceneFailure(s)
		}
		output.receiveRenameSceneResponse(response)
	}

	private suspend fun renameScene(request: RenameScene.RequestModel): RenameScene.ResponseModel {
		val scene = getScene(request.sceneId, request.locale)
		updateSceneIfNeeded(scene, request.name)
		return RenameScene.ResponseModel(scene.id.uuid, request.name.value)
	}

	private suspend fun getScene(sceneId: UUID, locale: Locale): Scene {
		return sceneRepository.getSceneById(Scene.Id(sceneId))
		  ?: throw SceneDoesNotExist(locale, sceneId)
	}

	private suspend fun updateSceneIfNeeded(scene: Scene, newName: NonBlankString)
	{
		if (scene.name != newName) {
			sceneRepository.updateScene(scene.withName(newName))
		}
	}
}