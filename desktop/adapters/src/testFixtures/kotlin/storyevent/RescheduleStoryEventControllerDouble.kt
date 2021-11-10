@file:Suppress("PackageDirectoryMismatch")
package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent
import io.mockk.spyk
import kotlinx.coroutines.Job


class RescheduleStoryEventControllerDouble : RescheduleStoryEventController by spyk()