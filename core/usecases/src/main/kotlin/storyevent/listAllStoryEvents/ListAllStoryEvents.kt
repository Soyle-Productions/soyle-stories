package com.soyle.stories.usecase.storyevent.listAllStoryEvents

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface ListAllStoryEvents {

	suspend operator fun invoke(projectId: Project.Id, output: OutputPort)

	class ResponseModel(storyEventItems: List<StoryEventItem>) : List<StoryEventItem> by storyEventItems

	fun interface OutputPort {
		suspend fun receiveListAllStoryEventsResponse(response: ResponseModel)
	}

}