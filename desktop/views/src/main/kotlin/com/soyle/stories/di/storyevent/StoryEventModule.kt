package com.soyle.stories.di.storyevent

import com.soyle.stories.common.listensTo
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.storyevent.addCharacterToStoryEvent.*
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventController
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventControllerImpl
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogController
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogModel
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogPresenter
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogViewListener
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventControllerImpl
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventControllerImpl
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventNotifier
import com.soyle.stories.storyevent.renameStoryEvent.RenameStoryEventController
import com.soyle.stories.storyevent.renameStoryEvent.RenameStoryEventControllerImpl
import com.soyle.stories.storyevent.renameStoryEvent.RenameStoryEventNotifier
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetailsUseCase
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEventsUseCase
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.storyevent.renameStoryEvent.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.renameStoryEvent.RenameStoryEventUseCase

object StoryEventModule {

	private fun InScope<ProjectScope>.usecases() {
		provide<CreateStoryEvent> {
			CreateStoryEventUseCase(get())
		}
		provide<ListAllStoryEvents> {
			ListAllStoryEventsUseCase(get())
		}
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
		provide<RenameStoryEvent> {
			RenameStoryEventUseCase(get())
		}
	}

	private fun InScope<ProjectScope>.notifiers() {
		provide(IncludedCharacterInStoryEventReceiver::class) {
			IncludedCharacterInStoryEventNotifier()
		}
		provide(CreateStoryEvent.OutputPort::class) {
			CreateStoryEventNotifier(applicationScope.get())
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
			RenameStoryEventNotifier(applicationScope.get())
		}
	}

	private fun InScope<ProjectScope>.controllers() {
		provide<CreateStoryEventController> {
			CreateStoryEventControllerImpl(
			  projectId.toString(),
			  applicationScope.get(),
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

		provide<RenameStoryEventController> {
			RenameStoryEventControllerImpl(
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

			provide<CreateStoryEventDialogViewListener> {
				CreateStoryEventDialogController(
				  CreateStoryEventDialogPresenter(
					get<CreateStoryEventDialogModel>(),
					get<CreateStoryEventNotifier>()
				  ),
				  get()
				)
			}

		}

		StoryEventListModule
		StoryEventDetailsModule
	}

}