package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.NormalizationPrompt
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface AdjustStoryEventsTimeController {

    fun adjustTimes(storyEventIds: Set<StoryEvent.Id>): Job
    fun adjustTimesBy(storyEventIds: Set<StoryEvent.Id>, amount: Long, confirmation: Boolean = false): Job

    companion object {
        fun Implementation(
            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            prompt: AdjustStoryEventsTimePrompt,
            normalizationPrompt: NormalizationPrompt,

            storyEventRepository: StoryEventRepository,

            adjustStoryEventsTime: AdjustStoryEventsTime,
            adjustStoryEventsTimeOutput: AdjustStoryEventsTime.OutputPort,
        ): AdjustStoryEventsTimeController =
            object : AdjustStoryEventsTimeController, CoroutineScope by CoroutineScope(guiContext) {
                override fun adjustTimes(storyEventIds: Set<StoryEvent.Id>): Job = launch {
                    prompt.use {
                        val confirmedAmount = prompt.requestAdjustmentAmount() ?: return@launch

                        if (attemptUseCase(storyEventIds, confirmedAmount)) return@launch

                        gatherUserInputs(storyEventIds, confirmedAmount, false)
                    }
                }

                override fun adjustTimesBy(storyEventIds: Set<StoryEvent.Id>, amount: Long, confirmation: Boolean): Job = launch {
                    prompt.use {
                        gatherUserInputs(storyEventIds, amount, confirmation)
                    }
                }

                private suspend fun attemptUseCase(storyEventIds: Set<StoryEvent.Id>, amount: Long): Boolean {
                    val affectedEvents = withContext(asyncContext) {
                        storyEventIds.mapNotNull { storyEventRepository.getStoryEventById(it) }
                    }

                    if (affectedEvents.none { it.time.toLong() + amount < 0 } || normalizationPrompt.confirmNormalization()) {
                        withContext(asyncContext) {
                            adjustStoryEventsTime(storyEventIds, amount, adjustStoryEventsTimeOutput)
                        }
                        return true
                    }
                    return false
                }

                private tailrec suspend fun gatherUserInputs(
                    storyEventIds: Set<StoryEvent.Id>, amount: Long, confirmation: Boolean
                ) {
                    val confirmedAmount = if (! confirmation) {
                        prompt.confirmAdjustmentAmount(amount) ?: return
                    } else amount

                    if (attemptUseCase(storyEventIds, confirmedAmount)) return
                    if (confirmation) return
                    gatherUserInputs(storyEventIds, confirmedAmount, confirmation)
                }
            }
    }

}