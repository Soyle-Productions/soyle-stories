package com.soyle.stories.usecase.storyevent.listAllStoryEvents

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.getOrderOfEventsInProject
import com.soyle.stories.usecase.storyevent.toItem
import java.util.*

class ListAllStoryEventsUseCase(
  private val storyEventRepository: StoryEventRepository
) : ListAllStoryEvents {

	override suspend fun invoke(projectId: Project.Id, output: ListAllStoryEvents.OutputPort) {
		storyEventRepository.listStoryEventsInProject(projectId)
			.map(StoryEvent::toItem)
			.let(ListAllStoryEvents::ResponseModel)
			.let(output::receiveListAllStoryEventsResponse)
	}
}