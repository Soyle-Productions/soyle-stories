package com.soyle.stories.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.getDialogPreference.GetDialogPreferenceController
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import kotlinx.coroutines.runBlocking

class RemoveStoryEventConfirmationPromptPresenter(
    private val storyEventIds: Set<StoryEvent.Id>,
    private val viewModel: RemoveStoryEventConfirmationPromptViewModel,
    private val getDialogPreference: GetDialogPreferenceController,
    private val removeStoryEventController: RemoveStoryEventController
) : RemoveStoryEventConfirmationPromptViewActions {

    override fun confirm() {
        viewModel.confirm()
        if (! viewModel.isConfirming) return
        sendConfirmation()
    }

    private fun sendConfirmation() {
        removeStoryEventController.confirmRemoveStoryEvent(storyEventIds, !viewModel.shouldNotShowAgain)
            .invokeOnCompletion(::endConfirmation)
    }

    private fun endConfirmation(potentialFailure: Throwable?) {
        if (potentialFailure == null) viewModel.complete()
        else viewModel.failed()
    }

    override fun cancel() {
        viewModel.cancel()
    }

    private inner class DialogPreferencesReceiver : GetDialogPreferences.OutputPort {
        override fun gotDialogPreferences(response: DialogPreference) {
            if (response.shouldShow) viewModel.needed()
            else {
                viewModel.unneeded()
                sendConfirmation()
            }
        }

        override fun failedToGetDialogPreferences(failure: Exception) {
            viewModel.needed()
        }
    }

    init {
        getDialogPreference.getPreferenceForDialog(
            DialogType.DeleteStoryEvent,
            DialogPreferencesReceiver()
        )
    }

}