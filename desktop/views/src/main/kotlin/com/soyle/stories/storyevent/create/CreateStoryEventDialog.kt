package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import tornadofx.UIComponent

interface CreateStoryEventDialog {

    data class Props(
        val relativePlacement: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null,
        val onCancelled: (() -> Unit)? = null,
        val onCreated: (() -> Unit)? = null
    )

    operator fun invoke(props: Props): UIComponent
}