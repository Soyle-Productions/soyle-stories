package com.soyle.stories.di.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventController
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventControllerImpl
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogController
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogModel
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogPresenter
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogViewListener
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEventUseCase

object StoryEventModule {

	init {
		scoped<ProjectScope> {

			provide<CreateStoryEvent> {
				CreateStoryEventUseCase(get())
			}

			provide {
				CreateStoryEventNotifier()
			}

			provide<CreateStoryEventController> {
				CreateStoryEventControllerImpl(
				  projectId.toString(),
				  applicationScope.get(),
				  get(),
				  get<CreateStoryEventNotifier>()
				)
			}

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
	}

}