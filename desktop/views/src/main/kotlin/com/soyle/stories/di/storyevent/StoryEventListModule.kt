package com.soyle.stories.di.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.CreateStoryEventOutput
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.renameStoryEvent.RenameStoryEventNotifier
import com.soyle.stories.storyevent.storyEventList.StoryEventListController
import com.soyle.stories.storyevent.storyEventList.StoryEventListPresenter
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewListener

object StoryEventListModule {

	init {

		scoped<ProjectScope> {

		}

	}

}