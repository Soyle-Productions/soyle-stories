package com.soyle.stories.di.storyevent

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterNotifier
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventNotifier
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventNotifier
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventNotifier
import com.soyle.stories.storyevent.storyEventDetails.*

object StoryEventDetailsModule {

	init {
		scoped<StoryEventDetailsScope> {

			provide {
				StoryEventDetailsPresenter(
				  storyEventId,
				  get<StoryEventDetailsModel>(),
				  projectScope.get<LinkLocationToStoryEventNotifier>(),
				  projectScope.get<AddCharacterToStoryEventNotifier>(),
				  projectScope.get<RemoveCharacterFromStoryEventNotifier>(),
				  projectScope.get<BuildNewCharacterNotifier>()
				)
			}

			provide<StoryEventDetailsViewListener> {
				StoryEventDetailsController(
				  storyEventId,
				  projectScope.applicationScope.get(),
				  projectScope.get(),
				  get<StoryEventDetailsPresenter>(),
				  projectScope.get(),
				  get<StoryEventDetailsPresenter>(),
				  projectScope.get(),
				  get<StoryEventDetailsPresenter>(),
				  projectScope.get(),
				  projectScope.get(),
				  projectScope.get()
				)
			}

		}
	}
}