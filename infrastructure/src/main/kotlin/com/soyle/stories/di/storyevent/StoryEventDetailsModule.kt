package com.soyle.stories.di.storyevent

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventNotifier
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.storyEventDetails.*
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetails

object StoryEventDetailsModule {

	init {
		scoped<StoryEventDetailsScope> {

			provide(
			  GetStoryEventDetails.OutputPort::class,
			  ListAllCharacterArcs.OutputPort::class,
			  ListAllLocations.OutputPort::class
			) {
				StoryEventDetailsPresenter(
				  storyEventId,
				  get<StoryEventDetailsModel>(),
				  projectScope.get<LinkLocationToStoryEventNotifier>(),
				  projectScope.get<AddCharacterToStoryEventNotifier>()
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
				  projectScope.get(),
				  get(),
				  projectScope.get(),
				  projectScope.get()
				)
			}

		}
	}
}