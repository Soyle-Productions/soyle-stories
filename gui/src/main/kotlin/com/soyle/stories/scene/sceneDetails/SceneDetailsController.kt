package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import java.util.*

class SceneDetailsController(
  sceneId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val getSceneDetails: GetSceneDetails,
  private val getSceneDetailsOutputPort: GetSceneDetails.OutputPort,
  private val linkLocationToSceneController: LinkLocationToSceneController,
  private val addCharacterToStoryEvent: AddCharacterToStoryEventController,
  private val removeCharacterFromStoryEventController: RemoveCharacterFromStoryEventController,
  private val setMotivationForCharacterInSceneController: SetMotivationForCharacterInSceneController,
  private val openToolController: OpenToolController
) : SceneDetailsViewListener {

	private val sceneId = UUID.fromString(sceneId)

	override fun getValidState() {
		threadTransformer.async {
			getSceneDetails.invoke(
			  GetSceneDetails.RequestModel(sceneId, localeManager.getCurrentLocale()),
			  getSceneDetailsOutputPort
			)
		}
	}

	override fun linkLocation(locationId: String) {
		linkLocationToSceneController.linkLocationToScene(sceneId.toString(), locationId)
	}

	override fun addCharacter(storyEventId: String, characterId: String) {
		addCharacterToStoryEvent.addCharacterToStoryEvent(storyEventId, characterId)
	}

	override fun removeCharacter(storyEventId: String, characterId: String) {
		removeCharacterFromStoryEventController.removeCharacter(storyEventId, characterId)
	}

	override fun setMotivation(characterId: String, motivation: String) {
		setMotivationForCharacterInSceneController.setMotivationForCharacter(sceneId.toString(), characterId, motivation)
	}

	override fun resetMotivation(characterId: String) {
		setMotivationForCharacterInSceneController.clearMotivationForCharacter(sceneId.toString(), characterId)
	}

	override fun openSceneDetails(sceneId: String) {
		openToolController.openSceneDetailsTool(sceneId)
	}

}