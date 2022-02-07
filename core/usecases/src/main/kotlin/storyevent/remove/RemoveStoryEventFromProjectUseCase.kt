package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class RemoveStoryEventFromProjectUseCase(
    private val storyEventRepository: StoryEventRepository
) : RemoveStoryEventFromProject {

    override suspend fun invoke(storyEventId: StoryEvent.Id, output: RemoveStoryEventFromProject.OutputPort) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)

        storyEventRepository.removeStoryEvent(storyEventId)

        output.storyEventRemovedFromProject(
            RemoveStoryEventFromProject.ResponseModel(
                StoryEventNoLongerHappens(storyEventId),
            )
        )
    }
}