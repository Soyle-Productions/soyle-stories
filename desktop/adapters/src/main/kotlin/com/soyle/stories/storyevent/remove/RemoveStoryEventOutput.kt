package com.soyle.stories.storyevent.remove

import com.soyle.stories.scene.outline.StoryEventRemovedFromSceneReceiver
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject

class RemoveStoryEventOutput(
    private val storyEventNoLongerHappensReceiver: StoryEventNoLongerHappensReceiver,
    private val storyEventRemovedFromSceneReceiver: StoryEventRemovedFromSceneReceiver
) : RemoveStoryEventFromProject.OutputPort {

    override suspend fun storyEventRemovedFromProject(response: RemoveStoryEventFromProject.ResponseModel) {
        storyEventNoLongerHappensReceiver.receiveStoryEventNoLongerHappens(response.storyEventNoLongerHappens)
        response.storyEventRemovedFromScene?.let {
            storyEventRemovedFromSceneReceiver.receiveStoryEventRemovedFromScene(
                it
            )
        }
    }
}