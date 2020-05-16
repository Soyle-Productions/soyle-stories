package com.soyle.stories.di.storyevent

import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventControllerImpl
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventNotifier
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
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEventUseCase
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEventUseCase
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEventUseCase
import com.soyle.stories.storyevent.usecases.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.storyevent.usecases.listAllStoryEvents.ListAllStoryEventsUseCase

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
	}

	private fun InScope<ProjectScope>.notifiers() {
		provide(CreateStoryEvent.OutputPort::class) {
			CreateStoryEventNotifier()
		}
		provide(LinkLocationToStoryEvent.OutputPort::class) {
			LinkLocationToStoryEventNotifier()
		}
		provide(AddCharacterToStoryEvent.OutputPort::class) {
			AddCharacterToStoryEventNotifier()
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