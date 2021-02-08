package com.soyle.stories.scene.linkLocationToScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene
import java.util.*

class LinkLocationToSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val linkLocationToScene: LinkLocationToScene,
  private val linkLocationToSceneOutputPort: LinkLocationToScene.OutputPort
) : LinkLocationToSceneController {

	override fun linkLocationToScene(sceneId: String, locationId: String) {
		val formattedSceneId = formatSceneID(sceneId)
		val formattedLocationId = formatLocationId(locationId)
		threadTransformer.async {
			linkLocationToScene.invoke(
			  LinkLocationToScene.RequestModel(
				formattedSceneId,
				formattedLocationId,
				localeManager.getCurrentLocale()
			  ),
			  linkLocationToSceneOutputPort
			)
		}
	}

	override fun clearLocationFromScene(sceneId: String) {
		val formattedSceneId = formatSceneID(sceneId)
		threadTransformer.async {
			linkLocationToScene.invoke(
			  LinkLocationToScene.RequestModel(
				formattedSceneId,
				null,
				localeManager.getCurrentLocale()
			  ),
			  linkLocationToSceneOutputPort
			)
		}
	}

	private fun formatSceneID(sceneId: String) = UUID.fromString(sceneId)
	private fun formatLocationId(locationId: String) = UUID.fromString(locationId)

}