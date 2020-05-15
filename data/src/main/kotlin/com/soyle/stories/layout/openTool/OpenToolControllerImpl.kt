package com.soyle.stories.layout.openTool

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.openTool.OpenTool
import java.util.*

class OpenToolControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val openTool: OpenTool,
  private val openToolOutputPort: OpenTool.OutputPort
) : OpenToolController {

	override fun openLocationDetailsTool(locationId: String) {
		threadTransformer.async {
			openTool.invoke(OpenTool.RequestModel.LocationDetails(
			  UUID.fromString(locationId)
			), openToolOutputPort)
		}
	}

	override fun openBaseStoryStructureTool(themeId: String, characterId: String) {
		threadTransformer.async {
			openTool.invoke(OpenTool.RequestModel.BaseStoryStructure(
			  UUID.fromString(characterId),
			  UUID.fromString(themeId)
			), openToolOutputPort)
		}
	}

	override fun openStoryEventDetailsTool(storyEventId: String) {
		threadTransformer.async {
			openTool.invoke(OpenTool.RequestModel.StoryEventDetails(
			  UUID.fromString(storyEventId)
			), openToolOutputPort)
		}
	}
}