package com.soyle.stories.di.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.storyevent.storyEventDetails.*

object StoryEventDetailsModule {

	init {
		scoped<StoryEventDetailsScope> {

			provide<StoryEventDetailsViewListener> {
				StoryEventDetailsController(
				  projectScope.applicationScope.get(),
				  projectScope.get(),
				  StoryEventDetailsPresenter(
					get<StoryEventDetailsModel>()
				  )
				)
			}

		}
	}
}