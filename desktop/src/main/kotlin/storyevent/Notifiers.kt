package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver

object Notifiers {

    init {
        scoped<ProjectScope> {
            storyEventCreated()
        }
    }

    private fun InProjectScope.storyEventCreated() {
        provide(StoryEventCreatedReceiver::class) {
            StoryEventCreatedNotifier()
        }
    }

}