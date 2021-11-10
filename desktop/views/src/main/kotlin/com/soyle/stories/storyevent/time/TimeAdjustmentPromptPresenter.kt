package com.soyle.stories.storyevent.time

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import kotlinx.coroutines.Job

class TimeAdjustmentPromptPresenter private constructor(
    private val storyEventIds: Set<StoryEvent.Id>,
    val viewModel: TimeAdjustmentPromptViewModel,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
    private val threadTransformer: ThreadTransformer
) : TimeAdjustmentPromptViewActions {

    interface Builder {
        operator fun invoke(storyEventIds: Set<StoryEvent.Id>): TimeAdjustmentPromptPresenter
        operator fun invoke(storyEventId: StoryEvent.Id, currentTime: Long): TimeAdjustmentPromptPresenter
        operator fun invoke(storyEventIds: Set<StoryEvent.Id>, initialAdjustment: Long): TimeAdjustmentPromptPresenter
    }

    companion object {

        operator fun invoke(
            rescheduleStoryEventController: RescheduleStoryEventController,
            adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
            threadTransformer: ThreadTransformer
        ): Builder = object : Builder {
            override fun invoke(storyEventIds: Set<StoryEvent.Id>): TimeAdjustmentPromptPresenter {
                return TimeAdjustmentPromptPresenter(storyEventIds, TimeAdjustmentPromptViewModel.adjustment(), rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)
            }

            override fun invoke(storyEventId: StoryEvent.Id, currentTime: Long): TimeAdjustmentPromptPresenter {
                return TimeAdjustmentPromptPresenter(setOf(storyEventId), TimeAdjustmentPromptViewModel.reschedule(currentTime), rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)
            }

            override fun invoke(storyEventIds: Set<StoryEvent.Id>, initialAdjustment: Long): TimeAdjustmentPromptPresenter {
                return TimeAdjustmentPromptPresenter(storyEventIds, TimeAdjustmentPromptViewModel.adjustment(initialAdjustment), rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)
            }
        }

    }

    //val viewModel = TimeAdjustmentPromptViewModel.reschedule(currentTime ?: 0)

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
        return if (viewModel.adjustment) {
            adjustStoryEventsTimeController.adjustTimesBy(storyEventIds, time)
        } else {
            rescheduleStoryEventController.rescheduleStoryEvent(storyEventIds.single(), time)
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