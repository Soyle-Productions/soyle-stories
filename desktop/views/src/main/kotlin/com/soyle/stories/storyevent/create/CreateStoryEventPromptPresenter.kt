package com.soyle.stories.storyevent.create

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import tornadofx.*

class CreateStoryEventPromptPresenter(
    private val relativePlacement: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null,
    private val createStoryEventController: CreateStoryEventController,
    private val threadTransformer: ThreadTransformer
) : CreateStoryEventPromptUserActions {

    val viewModel = CreateStoryEventPromptViewModel(relativePlacement != null)

    override fun createStoryEvent() {
        if (! viewModel.isValid.value) return
        if (viewModel.isCreating.value) return
        val name = NonBlankString.create(viewModel.name.value) ?: return
        viewModel.isCreating.set(true)
        val job = if (relativePlacement == null) {
            val time = viewModel.timeText.value?.toLongOrNull()
            if (time == null) {
                createStoryEventController.createStoryEvent(name)
            } else {
                createStoryEventController.createStoryEvent(name, time)
            }
        } else {
            createStoryEventController.createStoryEvent(name, relativePlacement)
        }
        job.invokeOnCompletion { failure ->
            threadTransformer.gui {
                if (failure != null) {
                    viewModel.isCreating.set(false)
                } else {
                    viewModel.isCompleted.set(true)
                }
            }
        }
    }

    override fun cancel() {
        viewModel.isCompleted.set(true)
    }

}