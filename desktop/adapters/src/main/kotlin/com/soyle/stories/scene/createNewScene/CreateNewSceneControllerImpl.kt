package com.soyle.stories.scene.createNewScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.*

class CreateNewSceneControllerImpl(
  projectId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val createNewScene: CreateNewScene,
  private val createNewSceneOutputPort: CreateNewScene.OutputPort
) : CreateNewSceneController {

	private val projectId = UUID.fromString(projectId)

	private fun output() = object : CreateNewScene.OutputPort by createNewSceneOutputPort {
		var response: CreateNewScene.ResponseModel? = null
		override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
			createNewSceneOutputPort.receiveCreateNewSceneResponse(response)
			this.response = response
		}
	}

	override fun createNewScene(name: NonBlankString): Deferred<Scene.Id> {
		val output = output()
		val deferred = CompletableDeferred<Scene.Id>()
		threadTransformer.async {
			val request = CreateNewScene.RequestModel(
			  name,
			  localeManager.getCurrentLocale()
			)
			createNewScene.invoke(request, output)
			if (output.response != null) deferred.complete(Scene.Id(output.response!!.sceneId))
		}
		return deferred
	}

	override fun createNewSceneBefore(name: NonBlankString, sceneId: String): Deferred<Scene.Id> {
		val output = output()
		val deferred = CompletableDeferred<Scene.Id>()
		threadTransformer.async {
			createNewScene.invoke(
			  CreateNewScene.RequestModel(
				name,
				UUID.fromString(sceneId),
				true,
				localeManager.getCurrentLocale()
			  ),
				output
			)
			if (output.response != null) deferred.complete(Scene.Id(output.response!!.sceneId))
		}
		return deferred
	}

	override fun createNewSceneAfter(name: NonBlankString, sceneId: String): Deferred<Scene.Id> {
		val output = output()
		val deferred = CompletableDeferred<Scene.Id>()
		threadTransformer.async {
			createNewScene.invoke(
			  CreateNewScene.RequestModel(
				name,
				UUID.fromString(sceneId),
				false,
				localeManager.getCurrentLocale()
			  ),
				output
			)
			if (output.response != null) deferred.complete(Scene.Id(output.response!!.sceneId))
		}
		return deferred
	}
}