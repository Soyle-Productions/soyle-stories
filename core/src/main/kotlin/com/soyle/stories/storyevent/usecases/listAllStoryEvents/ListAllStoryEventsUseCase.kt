package com.soyle.stories.storyevent.usecases.listAllStoryEvents

import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.usecases.getOrderOfEventsInProject
import com.soyle.stories.storyevent.usecases.toItem
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