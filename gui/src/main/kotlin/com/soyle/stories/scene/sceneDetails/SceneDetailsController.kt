package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import java.util.*

class SceneDetailsController(
  sceneId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val getSceneDetails: GetSceneDetails,
  private val getSceneDetailsOutputPort: GetSceneDetails.OutputPort,
  private val addCharacterToStoryEvent: AddCharacterToStoryEventController
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

	override fun addCharacter(storyEventId: String, characterId: String) {
		addCharacterToStoryEvent.addCharacterToStoryEvent(storyEventId, characterId)
	}

}