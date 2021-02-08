package com.soyle.stories.scene.renameScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import java.util.*

class RenameSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val renameScene: RenameScene,
  private val renameSceneOutputPort: RenameScene.OutputPort
) : RenameSceneController {

	override fun renameScene(sceneId: String, newName: NonBlankString) {
		val formattedSceneId = formatSceneId(sceneId)
		threadTransformer.async {
			val request = makeRequest(formattedSceneId, newName)
			renameScene.invoke(request, renameSceneOutputPort)
		}
	}

	private fun formatSceneId(sceneId: String) = UUID.fromString(sceneId)

	private suspend fun makeRequest(formattedSceneId: UUID, newName: NonBlankString): RenameScene.RequestModel {
		return RenameScene.RequestModel(
		  sceneId = formattedSceneId,
		  name = newName,
		  locale = localeManager.getCurrentLocale()
		)
	}

}