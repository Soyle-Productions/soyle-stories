package com.soyle.stories.usecase.storyevent.listAllStoryEvents

import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.getOrderOfEventsInProject
import com.soyle.stories.usecase.storyevent.toItem
import java.util.*

class ListAllStoryEventsUseCase(
  private val storyEventRepository: StoryEventRepository
) : ListAllStoryEvents {

	override suspend fun invoke(projectId: UUID, output: ListAllStoryEvents.OutputPort) {
		getOrderOfEventsInProject(storyEventRepository, projectId)
		  .mapIndexed { index, it ->
			  it.toItem(index)
		  }
		  .let(ListAllStoryEvents::ResponseModel)
		  .let(output::receiveListAllStoryEventsResponse)
	}
}