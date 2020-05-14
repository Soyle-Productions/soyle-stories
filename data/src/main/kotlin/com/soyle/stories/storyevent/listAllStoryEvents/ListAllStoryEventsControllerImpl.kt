package com.soyle.stories.storyevent.listAllStoryEvents

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.usecases.listAllStoryEvents.ListAllStoryEvents
import java.util.*

class ListAllStoryEventsControllerImpl(
  private val threadTransformer: ThreadTransformer,
  projectId: String,
  private val listAllStoryEvents: ListAllStoryEvents,
  private val listAllStoryEventsOutputPort: ListAllStoryEvents.OutputPort
) : ListAllStoryEventsController {

	private val projectId = UUID.fromString(projectId)

	override fun getValidState() {
		threadTransformer.async {
			listAllStoryEvents.invoke(projectId, listAllStoryEventsOutputPort)
		}
	}

}