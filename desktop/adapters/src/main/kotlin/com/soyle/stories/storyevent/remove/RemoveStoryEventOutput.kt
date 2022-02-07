package com.soyle.stories.storyevent.remove

import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject

class RemoveStoryEventOutput(
    private val storyEventNoLongerHappensReceiver: StoryEventNoLongerHappensReceiver
) : RemoveStoryEventFromProject.OutputPort {

    override suspend fun storyEventRemovedFromProject(response: RemoveStoryEventFromProject.ResponseModel) {
        storyEventNoLongerHappensReceiver.receiveStoryEventNoLongerHappens(response.storyEventNoLongerHappens)
    }
}