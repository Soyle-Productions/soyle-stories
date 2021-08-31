package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import java.util.*

class StoryEventListController(
  private val threadTransformer: ThreadTransformer,
  projectId: String,
  private val listAllStoryEvents: ListAllStoryEvents,
  private val presenter: ListAllStoryEvents.OutputPort,
  private val openToolController: OpenToolController,
  private val renameStoryEventController: RenameStoryEventController
) : StoryEventListViewListener {

	private val projectId = Project.Id(UUID.fromString(projectId))

	override fun getValidState() {
		threadTransformer.async {
			listAllStoryEvents.invoke(projectId, presenter)
		}
	}

	override fun openStoryEventDetails(storyEventId: String) {
		openToolController.openStoryEventDetailsTool(storyEventId)
	}

	override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString) {
		renameStoryEventController.renameStoryEvent(storyEventId, newName)
	}

}