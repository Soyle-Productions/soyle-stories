package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString

class RenameStoryEventPromptPresenter(
    private val storyEventId: StoryEvent.Id,
    currentName: String,
    private val renameStoryEventController: RenameStoryEventController,
    private val threadTransformer: ThreadTransformer
) : RenameStoryEventPromptUserActions {

    val viewModel = RenameStoryEventPromptViewModel(currentName)

    /**
     * if the [viewModel] is in a valid state, will disable the viewModel and attempt to rename the story event.
     * If the async job is a failure, the viewModel will be enabled.  Otherwise, it will be completed.
     */
    override fun rename() {
        viewModel.disable()
        if (!viewModel.isValid.value) return
        val name = NonBlankString.create(viewModel.name.value) ?: return
        renameStoryEventController.renameStoryEvent(storyEventId, name)
            .invokeOnCompletion { failure ->
                threadTransformer.gui {
                    if (failure != null) viewModel.enable()
                    else viewModel.complete()
                }
            }
    }

    /**
     * Immediately puts the [viewModel] into the completed state
     */
    override fun cancel() {
        viewModel.cancel()
    }

}