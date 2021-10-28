package com.soyle.stories.desktop.adapter.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import kotlinx.coroutines.Job
import kotlin.reflect.KFunction

class RemoveStoryEventControllerDouble private constructor(
    private val spy: RemoveStoryEventController
) : RemoveStoryEventController by spy {
    constructor() : this(mockk())

    val requestedStoryEventIds = slot<Set<StoryEvent.Id>>()

    val confirmedStoryEventIds = slot<Set<StoryEvent.Id>>()
    val showShowConfirmation = slot<Boolean>()

    init {
        every { spy.removeStoryEvent(capture(requestedStoryEventIds)) } returns Unit
        every { spy.confirmRemoveStoryEvent(capture(confirmedStoryEventIds), capture(showShowConfirmation)) } returns Job()
    }

}