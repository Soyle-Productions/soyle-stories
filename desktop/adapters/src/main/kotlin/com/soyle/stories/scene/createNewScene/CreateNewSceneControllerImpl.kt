package com.soyle.stories.scene.createNewScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import java.util.*

class CreateNewSceneControllerImpl(
  projectId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val createNewScene: CreateNewScene,
  private val createNewSceneOutputPort: CreateNewScene.OutputPort
) : CreateNewSceneController {

	private val projectId = UUID.fromString(projectId)

	override fun createNewScene(name: NonBlankString) {
		threadTransformer.async {
			val request = CreateNewScene.RequestModel(
			  name,
			  localeManager.getCurrentLocale()
			)
			createNewScene.invoke(request, createNewSceneOutputPort)
		}
	}

	override fun createNewSceneBefore(name: NonBlankString, sceneId: String) {
		threadTransformer.async {
			createNewScene.invoke(
			  CreateNewScene.RequestModel(
				name,
				UUID.fromString(sceneId),
				true,
				localeManager.getCurrentLocale()
			  ),
			  createNewSceneOutputPort
			)
		}
	}

	override fun createNewSceneAfter(name: NonBlankString, sceneId: String) {
		threadTransformer.async {
			createNewScene.invoke(
			  CreateNewScene.RequestModel(
				name,
				UUID.fromString(sceneId),
				false,
				localeManager.getCurrentLocale()
			  ),
			  createNewSceneOutputPort
			)
		}
	}
}