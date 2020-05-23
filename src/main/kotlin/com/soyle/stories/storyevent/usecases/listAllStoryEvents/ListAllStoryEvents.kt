package com.soyle.stories.storyevent.usecases.listAllStoryEvents

import com.soyle.stories.storyevent.usecases.StoryEventItem
import java.util.*

interface ListAllStoryEvents {

	suspend operator fun invoke(projectId: UUID, output: OutputPort)

	class ResponseModel(val storyEventItems: List<StoryEventItem>)

	interface OutputPort {
		fun receiveListAllStoryEventsResponse(response: ResponseModel)
	}

}