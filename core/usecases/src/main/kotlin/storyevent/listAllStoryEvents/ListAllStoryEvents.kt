package com.soyle.stories.usecase.storyevent.listAllStoryEvents

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface ListAllStoryEvents {

	suspend operator fun invoke(projectId: Project.Id, output: OutputPort)

	class ResponseModel(val storyEventItems: List<StoryEventItem>)

	fun interface OutputPort {
		fun receiveListAllStoryEventsResponse(response: ResponseModel)
	}

}