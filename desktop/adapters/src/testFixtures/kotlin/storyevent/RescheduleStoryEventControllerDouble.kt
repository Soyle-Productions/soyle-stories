@file:Suppress("PackageDirectoryMismatch")
package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent
import kotlinx.coroutines.Job


class RescheduleStoryEventControllerDouble : RescheduleStoryEventController {

    override fun requestToRescheduleStoryEvent(storyEventId: StoryEvent.Id, currentTime: Long) {

    }

    override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job {
        TODO("Not yet implemented")
    }
}