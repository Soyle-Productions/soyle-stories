package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool

object Presentation {

    init {
        scoped<ProjectScope> {
            provide { CreateStoryEventForm(get()) }
            provide { StoryEventListTool({ get() }) }
        }
    }

}