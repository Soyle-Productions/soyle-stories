package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.usecases.listAllStoryEvents.ListAllStoryEvents
import java.util.*

class StoryEventListController(
  private val threadTransformer: ThreadTransformer,
  projectId: String,
  private val listAllStoryEvents: ListAllStoryEvents,
  private val presenter: ListAllStoryEvents.OutputPort
) : StoryEventListViewListener {

	private val projectId: UUID = UUID.fromString(projectId)

	override fun getValidState() {
		threadTransformer.async {
			listAllStoryEvents.invoke(projectId, presenter)
		}
	}

}