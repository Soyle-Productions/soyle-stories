package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

interface RemoveStoryEventController {

    fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>)
    fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>, shouldShowAgain: Boolean): Job

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            removeStoryEventFromProject: RemoveStoryEventFromProject,
            output: RemoveStoryEventFromProject.OutputPort,
            setDialogPreferences: SetDialogPreferences,
            setDialogPreferencesOutputPort: SetDialogPreferences.OutputPort,
            confirmation: RemoveStoryEventConfirmation
        ) = object : RemoveStoryEventController {
            override fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>) = confirmation.requestDeleteStoryEventConfirmation(storyEventIds)

            override fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>, shouldShowAgain: Boolean): Job {
                threadTransformer.async {
                    setDialogPreferences(DialogType.DeleteStoryEvent, shouldShowAgain, setDialogPreferencesOutputPort)
                }

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