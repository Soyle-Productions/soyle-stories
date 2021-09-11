package com.soyle.stories.storyevent.time

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import kotlinx.coroutines.Job

class TimeAdjustmentPromptPresenter(
    private val storyEventIds: Set<StoryEvent.Id>,
    currentTime: Long? = null,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
    private val threadTransformer: ThreadTransformer
): TimeAdjustmentPromptViewActions {

    val viewModel = TimeAdjustmentPromptViewModel(currentTime)

    private fun canSubmit() = viewModel.canSubmit.value

    override fun submit() {
        if (! canSubmit()) return
        val time = viewModel.time.value.toLongOrNull() ?: return
        startSubmission(time)
    }

    override fun cancel() {
        viewModel.success()
    }

    private fun startSubmission(time: Long) {
        viewModel.submitting()
        val job = getSubmissionJob(time)
        job.invokeOnCompletion(::endSubmission)
    }

    private fun getSubmissionJob(time: Long): Job {
        return if (storyEventIds.onlyHasOneItem()) {
            rescheduleStoryEventController.rescheduleStoryEvent(storyEventIds.single(), time)
        } else {
            adjustStoryEventsTimeController.adjustStoryEventsTime(storyEventIds, time)
        }
    }

    private fun endSubmission(potentialFailure: Throwable?) {
        threadTransformer.gui {
            if (potentialFailure != null) viewModel.failed()
            else viewModel.success()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Collection<*>.onlyHasOneItem(): Boolean = size == 1

}