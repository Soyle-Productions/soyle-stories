package com.soyle.stories.di.storyevent

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.storyEventDetails.*

object StoryEventDetailsModule {

	init {
		scoped<StoryEventDetailsScope> {

			provide(ListAllCharacterArcs.OutputPort::class, ListAllLocations.OutputPort::class) {
				StoryEventDetailsPresenter(
				  get<StoryEventDetailsModel>(),
				  projectScope.get<LinkLocationToStoryEventNotifier>()
				)
			}

			provide<StoryEventDetailsViewListener> {
				StoryEventDetailsController(
				  storyEventId,
				  projectScope.applicationScope.get(),
				  projectScope.get(),
				  get(),
				  projectScope.get(),
				  get(),
				  projectScope.get()
				)
			}

		}
	}
}