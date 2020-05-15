package com.soyle.stories.di.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier
import com.soyle.stories.storyevent.storyEventList.StoryEventListController
import com.soyle.stories.storyevent.storyEventList.StoryEventListModel
import com.soyle.stories.storyevent.storyEventList.StoryEventListPresenter
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewListener

object StoryEventListModule {

	init {

		scoped<ProjectScope> {

			provide<StoryEventListViewListener> {
				StoryEventListController(
				  applicationScope.get(),
				  projectId.toString(),
				  get(),
				  StoryEventListPresenter(
					get<StoryEventListModel>(),
					get<CreateStoryEventNotifier>()
				  )
				)
			}

		}

	}

}