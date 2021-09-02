package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import tornadofx.runLater

class RemoveStoryEventConfirmationDialog(
    private val threadTransformer: ThreadTransformer,
    private val removeStoryEventController: RemoveStoryEventController,
    private val getDialogPreferences: GetDialogPreferences,
    private val createDialog: (Set<StoryEvent.Id>) -> RemoveStoryEventConfirmationDialogView
) : RemoveStoryEventConfirmation {

    override fun requestDeleteStoryEventConfirmation(storyEventIds: Set<StoryEvent.Id>) {
        threadTransformer.async {
            getDialogPreferences.invoke(DialogType.DeleteStoryEvent, object : GetDialogPreferences.OutputPort {
                override fun gotDialogPreferences(response: DialogPreference) {
                    if (response.shouldShow) runLater { createDialog(storyEventIds) }
                    else removeStoryEventController.confirmRemoveStoryEvent(storyEventIds)
                }

                override fun failedToGetDialogPreferences(failure: Exception) {
                    println(failure)
                    runLater { createDialog(storyEventIds) }
                }
            })
        }
    }

}