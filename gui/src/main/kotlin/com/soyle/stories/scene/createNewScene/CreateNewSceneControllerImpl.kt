package com.soyle.stories.scene.createNewScene

import com.soyle.stories.gui.LocaleManager
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene

class CreateNewSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val createNewScene: CreateNewScene,
  private val createNewSceneOutputPort: CreateNewScene.OutputPort
) : CreateNewSceneController {

	override fun createNewScene(name: String) {
		threadTransformer.async {
			createNewScene.invoke(name, localeManager.getCurrentLocale(), createNewSceneOutputPort)
		}
	}
}