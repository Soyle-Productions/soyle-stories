package com.soyle.stories.desktop.adapter.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.Job

class CreateStoryEventControllerDouble(
    private val onCreateStoryEvent: (NonBlankString, Long?) -> Job = { _, _ -> Job() },
    private val onCreateStoryEventRelativeTo: (NonBlankString, CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative) -> Job = { _, _ -> Job() }
) : CreateStoryEventController {

    override fun requestToCreateStoryEvent() {
        TODO("Not yet implemented")
    }

    override fun requestToCreateStoryEvent(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative) {
        TODO("Not yet implemented")
    }

    override fun createStoryEvent(name: NonBlankString): Job = onCreateStoryEvent(name, null)

    override fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job = onCreateStoryEvent(name, timeUnit)

    override fun createStoryEvent(
        name: NonBlankString,
        relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative
    ): Job = onCreateStoryEventRelativeTo(name, relativeTo)
}