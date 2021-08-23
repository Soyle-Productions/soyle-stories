package com.soyle.stories.desktop.adapter.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import kotlinx.coroutines.Job

class CreateStoryEventControllerDouble(
    private val onCreateStoryEvent: (NonBlankString, Long?) -> Job = { _, _ -> Job() },
    private val onCreateStoryEventBefore: (NonBlankString, String) -> Unit = { _, _ -> },
    private val onCreateStoryEventAfter: (NonBlankString, String) -> Unit = { _, _ -> }
) : CreateStoryEventController {

    override fun createStoryEvent(name: NonBlankString): Job = onCreateStoryEvent(name, null)

    override fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job = onCreateStoryEvent(name, timeUnit)

    override fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String) = onCreateStoryEventBefore(name, relativeStoryEventId)

    override fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String) = onCreateStoryEventAfter(name, relativeStoryEventId)
}