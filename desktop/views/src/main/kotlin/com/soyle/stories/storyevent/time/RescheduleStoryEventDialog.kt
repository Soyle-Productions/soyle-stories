package com.soyle.stories.storyevent.time

import com.soyle.stories.domain.storyevent.StoryEvent
import tornadofx.Component

/** Opens a dialog that allows the user to reschedule one or more story events */
fun interface RescheduleStoryEventDialog {

    class Props(val storyEventId: StoryEvent.Id, val currentTime: Long)

    operator fun invoke(props: Props)

}