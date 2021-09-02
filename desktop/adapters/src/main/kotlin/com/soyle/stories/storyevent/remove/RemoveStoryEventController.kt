package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

interface RemoveStoryEventController {

    fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>)
    fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>): Job

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            removeStoryEventFromProject: RemoveStoryEventFromProject,
            output: RemoveStoryEventFromProject.OutputPort,
            confirmation: RemoveStoryEventConfirmation
        ) = object : RemoveStoryEventController {
            override fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>) = confirmation.requestDeleteStoryEventConfirmation(storyEventIds)

            override fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>): Job {
                return threadTransformer.async {
                    storyEventIds.map {
                        launch {
                            removeStoryEventFromProject.invoke(it, output)
                        }
                    }.joinAll()
                }
            }
        }
    }
}