package com.soyle.stories.usecase.storyevent.listAllStoryEvents

import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface ListAllStoryEvents {

	suspend operator fun invoke(projectId: UUID, output: OutputPort)

	class ResponseModel(val storyEventItems: List<StoryEventItem>)

	interface OutputPort {
		fun receiveListAllStoryEventsResponse(response: ResponseModel)
	}

}