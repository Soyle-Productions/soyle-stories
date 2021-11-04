package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime
import kotlinx.coroutines.Job

interface AdjustStoryEventsTimeController {

    fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>)
    fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>, amount: Long)
    fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            adjustStoryEventsTime: AdjustStoryEventsTime,
            adjustStoryEventsTimeOutput: AdjustStoryEventsTime.OutputPort,

            adjustStoryEventsTimePrompt: AdjustStoryEventsTimePrompt
        ) = object : AdjustStoryEventsTimeController {
            override fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>) {
                adjustStoryEventsTimePrompt.promptForAdjustmentAmount(storyEventIds)
            }

            override fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>, amount: Long) {
                adjustStoryEventsTimePrompt.promptForAdjustmentAmount(storyEventIds, amount)
            }

            override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
                return threadTransformer.async {
                    adjustStoryEventsTime.invoke(storyEventIds, amount, adjustStoryEventsTimeOutput)
                }
            }
        }
    }

}