package com.soyle.stories.storyevent.time

import com.soyle.stories.domain.storyevent.StoryEvent
import tornadofx.Component

/** Opens a dialog that allows the user to reschedule one or more story events */
fun interface RescheduleStoryEventDialog {

    sealed class Props {
        companion object {
            operator fun invoke(storyEventId: StoryEvent.Id, currentTime: Long) = Reschedule(storyEventId, currentTime)
        }
    }
    class Reschedule(val storyEventId: StoryEvent.Id, val currentTime: Long) : Props()
    class AdjustTimes(val storyEventIds: Set<StoryEvent.Id>) : Props()

    operator fun invoke(props: Props)

}