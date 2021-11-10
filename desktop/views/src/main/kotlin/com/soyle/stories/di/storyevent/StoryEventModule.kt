package com.soyle.stories.di.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.storyevent.addCharacterToStoryEvent.*
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogController
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogPresenter
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogViewListener
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventControllerImpl
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventControllerImpl
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventNotifier
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventOutput
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetailsUseCase
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEventUseCase

object StoryEventModule {

    private fun InScope<ProjectScope>.usecases() {
        provide<LinkLocationToStoryEvent> {
            LinkLocationToStoryEventUseCase(get(), get())
        }
        provide<AddCharacterToStoryEvent> {
            AddCharacterToStoryEventUseCase(get(), get())
        }
        provide<GetStoryEventDetails> {
            GetStoryEventDetailsUseCase(get())
        }
        provide<RemoveCharacterFromStoryEvent> {
            RemoveCharacterFromStoryEventUseCase(get())
        }
    }

    private fun InScope<ProjectScope>.notifiers() {
        provide(IncludedCharacterInStoryEventReceiver::class) {
            IncludedCharacterInStoryEventNotifier()
        }
        provide(LinkLocationToStoryEvent.OutputPort::class) {
            LinkLocationToStoryEventNotifier(applicationScope.get())
        }
        provide(AddCharacterToStoryEvent.OutputPort::class) {
            get<IncludeCharacterInSceneControllerImpl>() listensTo get<IncludedCharacterInStoryEventNotifier>()

            AddCharacterToStoryEventOutput(applicationScope.get(), get())
        }
        provide(RemoveCharacterFromStoryEvent.OutputPort::class) {
            RemoveCharacterFromStoryEventNotifier(applicationScope.get()).also {
                get<RemoveCharacterFromSceneControllerImpl>() listensTo it
            }
        }
        provide(RenameStoryEvent.OutputPort::class) {
            RenameStoryEventOutput(applicationScope.get())
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

        provide<AddCharacterToStoryEventController> {
            AddCharacterToStoryEventControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(RemoveCharacterFromStoryEventController::class) {
            RemoveCharacterFromStoryEventControllerImpl(
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
        StoryEventDetailsModule
    }

}