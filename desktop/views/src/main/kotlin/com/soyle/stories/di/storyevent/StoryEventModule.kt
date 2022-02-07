package com.soyle.stories.di.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.character.add.IncludedCharacterInStoryEventNotifier
import com.soyle.stories.storyevent.character.add.IncludedCharacterInStoryEventReceiver
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventControllerImpl
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEventUseCase

object StoryEventModule {

    private fun InScope<ProjectScope>.usecases() {
        provide<LinkLocationToStoryEvent> {
            LinkLocationToStoryEventUseCase(get(), get())
        }
    }

    private fun InScope<ProjectScope>.notifiers() {
        provide(IncludedCharacterInStoryEventReceiver::class) {
            IncludedCharacterInStoryEventNotifier()
        }
        provide(LinkLocationToStoryEvent.OutputPort::class) {
            LinkLocationToStoryEventNotifier(applicationScope.get())
        }
    }

    private fun InScope<ProjectScope>.controllers() {
        provide<CreateStoryEventController> {
            CreateStoryEventController.Implementation(
                Project.Id(projectId),
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        provide<LinkLocationToStoryEventController> {
            LinkLocationToStoryEventControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

    }

    init {
        scoped<ProjectScope> {

            usecases()
            notifiers()
            controllers()

        }

        StoryEventListModule
    }

}