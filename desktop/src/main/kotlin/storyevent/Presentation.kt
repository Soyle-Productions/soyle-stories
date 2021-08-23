package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool

object Presentation {

    init {
        scoped<ProjectScope> {
            provide {
                StoryEventListTool(
                    projectId = Project.Id(projectId),
                    createStoryEventFormFactory = { CreateStoryEventScope(it, this).get() },
                    listStoryEventsInProject = get(),
                    storyEventCreated = get<StoryEventCreatedNotifier>()
                )
            }
        }
        scoped<CreateStoryEventScope> {
            provide<CreateStoryEventForm> { createStoryEventForm }
        }
    }

}