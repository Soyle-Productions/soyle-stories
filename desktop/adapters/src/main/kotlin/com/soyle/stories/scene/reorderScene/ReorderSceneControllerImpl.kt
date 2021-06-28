package com.soyle.stories.scene.reorderScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import java.util.*

class ReorderSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val reorderScene: ReorderScene,
  private val reorderSceneOutputPort: ReorderScene.OutputPort
) : ReorderSceneController {

	override fun reorderScene(sceneId: String, newIndex: Int) {
		threadTransformer.async {
			reorderScene.invoke(
			  ReorderScene.RequestModel(
				UUID.fromString(sceneId),
				newIndex,
				localeManager.getCurrentLocale()
			  ),
			  reorderSceneOutputPort
			)
		}
	}

}