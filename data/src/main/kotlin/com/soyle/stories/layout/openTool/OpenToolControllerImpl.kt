package com.soyle.stories.layout.openTool

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.tools.dynamic.BaseStoryStructure
import com.soyle.stories.layout.tools.dynamic.LocationDetails
import com.soyle.stories.layout.tools.dynamic.StoryEventDetails
import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.layout.usecases.openTool.OpenTool
import java.util.*

class OpenToolControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val openTool: OpenTool,
  private val openToolOutputPort: OpenTool.OutputPort
) : OpenToolController {

	override fun openLocationDetailsTool(locationId: String) {
		threadTransformer.async {
			openTool.invoke(
			  LocationDetails(UUID.fromString(locationId)),
			  openToolOutputPort
			)
		}
	}

	override fun openBaseStoryStructureTool(themeId: String, characterId: String) {
		threadTransformer.async {
			openTool.invoke(
			  BaseStoryStructure(
				UUID.fromString(characterId),
				UUID.fromString(themeId)
			  ),
			  openToolOutputPort
			)
		}
	}

	override fun openStoryEventDetailsTool(storyEventId: String) {
		threadTransformer.async {
			openTool.invoke(
			  StoryEventDetails(
				UUID.fromString(storyEventId)
			  ),
			  openToolOutputPort
			)
		}
	}

	override fun openDeleteSceneRamificationsTool(sceneId: String) {
		threadTransformer.async {
			openTool.invoke(
			  Ramifications.DeleteSceneRamifications(
				UUID.fromString(sceneId),
				localeManager.getCurrentLocale()
			  ),
			  openToolOutputPort
			)
		}
	}
}