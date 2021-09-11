package com.soyle.stories.storyevent.time

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import kotlinx.coroutines.Job

class TimeAdjustmentPromptPresenter private constructor(
    private val storyEventIds: Set<StoryEvent.Id>,
    private val currentTime: Long?,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
    private val threadTransformer: ThreadTransformer
) : TimeAdjustmentPromptViewActions {

    // rescheduling
    constructor(
        storyEventId: StoryEvent.Id, currentTime: Long, rescheduleStoryEventController: RescheduleStoryEventController,
        adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
        threadTransformer: ThreadTransformer
    ) : this(
        setOf(storyEventId),
        currentTime,
        rescheduleStoryEventController,
        adjustStoryEventsTimeController,
        threadTransformer
    )

    // adjusting the time
    constructor(
        storyEventIds: Set<StoryEvent.Id>, rescheduleStoryEventController: RescheduleStoryEventController,
        adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
        threadTransformer: ThreadTransformer
    ) : this(storyEventIds, null, rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)

    val viewModel = TimeAdjustmentPromptViewModel(currentTime)

    private fun canSubmit() = viewModel.canSubmit.value

    override fun submit() {
        if (!canSubmit()) return
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
        return if (currentTime != null) {
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