package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RemoveStoryEventFromProjectUseCase(
    private val storyEventRepository: StoryEventRepository
) : RemoveStoryEventFromProject {

    override suspend fun invoke(storyEventId: StoryEvent.Id, output: RemoveStoryEventFromProject.OutputPort) {
        storyEventRepository.getStoryEventOrError(storyEventId)
        storyEventRepository.removeStoryEvent(storyEventId)
        output.storyEventRemovedFromProject(RemoveStoryEventFromProject.ResponseModel(StoryEventNoLongerHappens(storyEventId)))
    }
}