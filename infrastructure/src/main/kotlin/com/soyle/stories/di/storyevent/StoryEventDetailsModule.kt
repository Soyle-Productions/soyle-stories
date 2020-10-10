package com.soyle.stories.di.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventOutput
import com.soyle.stories.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEventNotifier
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.storyEventDetails.*

object StoryEventDetailsModule {

	init {
		scoped<StoryEventDetailsScope> {

			provide {
				StoryEventDetailsPresenter(
				  storyEventId,
				  get<StoryEventDetailsModel>(),
				  projectScope.get<LinkLocationToStoryEventNotifier>(),
				  projectScope.get<IncludedCharacterInStoryEventNotifier>()
				)
			}

			provide<StoryEventDetailsViewListener> {
				StoryEventDetailsController(
				  storyEventId = storyEventId,
				  threadTransformer = projectScope.applicationScope.get(),
				  getStoryEventDetails = projectScope.get(),
				  getStoryEventDetailsOutputPort = get<StoryEventDetailsPresenter>(),
				  liveCharacterList = projectScope.get(),
				  characterListListener = get<StoryEventDetailsPresenter>(),
				  liveLocationList = projectScope.get(),
				  locationListListener = get<StoryEventDetailsPresenter>(),
				  linkLocationToStoryEventController = projectScope.get(),
				  addCharacterToStoryEventController = projectScope.get(),
				  removeCharacterFromStoryEventController = projectScope.get()
				)
			}

		}
	}
}