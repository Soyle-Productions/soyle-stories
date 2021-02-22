package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.usecase.scene.getSceneDetails.GetSceneDetails
import java.util.*

class SceneDetailsController(
  sceneId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val getSceneDetails: GetSceneDetails,
  private val getSceneDetailsOutputPort: GetSceneDetails.OutputPort,
  private val linkLocationToSceneController: LinkLocationToSceneController,
) : SceneDetailsViewListener {

	private val sceneId = Scene.Id(UUID.fromString(sceneId))

	override fun getValidState() {
		threadTransformer.async {
			getSceneDetails.invoke(
			  GetSceneDetails.RequestModel(sceneId.uuid, localeManager.getCurrentLocale()),
			  getSceneDetailsOutputPort
			)
		}
	}

	override fun linkLocation(locationId: Location.Id) {
		linkLocationToSceneController.linkLocationToScene(sceneId, locationId)
	}
}