package com.soyle.stories.storyevent.renameStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.renameStoryEvent.RenameStoryEvent
import java.util.*

class RenameStoryEventControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val renameStoryEvent: RenameStoryEvent,
  private val renameStoryEventOutputPort: RenameStoryEvent.OutputPort
) : RenameStoryEventController {

	override fun renameStoryEvent(storyEventId: String, newName: String) {
		val formattedStoryEventId = formatStoryEventId(storyEventId)
		threadTransformer.async {
			renameStoryEvent.invoke(formattedStoryEventId, newName, renameStoryEventOutputPort)
		}
	}

	private fun formatStoryEventId(storyEventId: String) = UUID.fromString(storyEventId)

}