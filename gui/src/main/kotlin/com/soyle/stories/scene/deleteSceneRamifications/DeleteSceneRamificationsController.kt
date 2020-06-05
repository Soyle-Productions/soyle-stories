package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import java.util.*

class DeleteSceneRamificationsController(
  sceneId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val getPotentialChangesFromDeletingScene: GetPotentialChangesFromDeletingScene,
  private val getPotentialChangesFromDeletingSceneOutputPort: GetPotentialChangesFromDeletingScene.OutputPort
) : DeleteSceneRamificationsViewListener {

	private val sceneId = UUID.fromString(sceneId)

	override fun getValidState() {
		threadTransformer.async {
			getPotentialChangesFromDeletingScene.invoke(
			  GetPotentialChangesFromDeletingScene.RequestModel(sceneId, localeManager.getCurrentLocale()),
			  getPotentialChangesFromDeletingSceneOutputPort
			)
		}
	}

}