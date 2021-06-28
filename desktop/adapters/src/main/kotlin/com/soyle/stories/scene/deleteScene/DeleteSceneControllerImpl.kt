package com.soyle.stories.scene.deleteScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import java.util.*

class DeleteSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val deleteScene: DeleteScene,
  private val deleteSceneOutputPort: DeleteScene.OutputPort
) : DeleteSceneController {
	override fun deleteScene(sceneId: String) {
		val formattedSceneId = formatSceneId(sceneId)
		threadTransformer.async {
			deleteScene.invoke(
			  formattedSceneId,
			  localeManager.getCurrentLocale(),
			  deleteSceneOutputPort
			)
		}
	}

	private fun formatSceneId(sceneId: String) = UUID.fromString(sceneId)
}