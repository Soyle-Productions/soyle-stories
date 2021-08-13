package com.soyle.stories.desktop.adapter.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import kotlinx.coroutines.Job

class CreateStoryEventControllerDouble(
    private val onCreateStoryEvent: (NonBlankString) -> Job = { Job() },
    private val onCreateStoryEventBefore: (NonBlankString, String) -> Unit = { _, _ -> },
    private val onCreateStoryEventAfter: (NonBlankString, String) -> Unit = { _, _ -> }
) : CreateStoryEventController {

    override fun createStoryEvent(name: NonBlankString): Job = onCreateStoryEvent(name)

    override fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String) = onCreateStoryEventBefore(name, relativeStoryEventId)

    override fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String) = onCreateStoryEventAfter(name, relativeStoryEventId)
}