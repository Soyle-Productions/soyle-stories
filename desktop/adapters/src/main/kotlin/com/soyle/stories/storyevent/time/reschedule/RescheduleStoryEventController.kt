package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.NormalizationPrompt
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface RescheduleStoryEventController {

    fun rescheduleStoryEvent(storyEventId: StoryEvent.Id): Job

    companion object {
        fun Implementation(
            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            newTimePrompt: RescheduleStoryEventPrompt,
            normalizationPrompt: NormalizationPrompt,

            storyEventRepository: StoryEventRepository,

            rescheduleStoryEvent: RescheduleStoryEvent,
            rescheduleStoryEventOutput: RescheduleStoryEvent.OutputPort,
        ): RescheduleStoryEventController = object : RescheduleStoryEventController, CoroutineScope by CoroutineScope(guiContext) {
            override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id): Job = launch {
                val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
                newTimePrompt.use {
                    rescheduleStoryEvent(storyEventId, storyEvent.time.toLong())
                }
            }

            private tailrec suspend fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, currentTime: Long) {
                val newTime = newTimePrompt.requestNewTime(currentTime) ?: return

                if (newTime > 0 || normalizationPrompt.confirmNormalization()) {
                    withContext(asyncContext) {
                        rescheduleStoryEvent(storyEventId, newTime, rescheduleStoryEventOutput)
                    }
                    return
                }
                rescheduleStoryEvent(storyEventId, currentTime)
            }

        }
    }

}