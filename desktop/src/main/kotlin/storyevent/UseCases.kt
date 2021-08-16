package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.CreateStoryEventOutput
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            createStoryEvent()
        }
    }

    private fun InProjectScope.createStoryEvent() {
        provide<CreateStoryEvent> { CreateStoryEventUseCase(get()) }
        provide<CreateStoryEvent.OutputPort> { CreateStoryEventOutput(get()) }
    }

}