package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime
import kotlinx.coroutines.Job

interface AdjustStoryEventsTimeController {

    fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            adjustStoryEventsTime: AdjustStoryEventsTime,
            adjustStoryEventsTimeOutput: AdjustStoryEventsTime.OutputPort
        ) = object : AdjustStoryEventsTimeController {
            override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
                return threadTransformer.async {
                    adjustStoryEventsTime.invoke(storyEventIds, amount, adjustStoryEventsTimeOutput)
                }
            }
        }
    }

}