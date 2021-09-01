package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver

object Notifiers {

    init {
        scoped<ProjectScope> {
            storyEventCreated()
            storyEventRenamed()
            storyEventRescheduled()
        }
    }

    private fun InProjectScope.storyEventCreated() {
        provide(StoryEventCreatedReceiver::class) {
            StoryEventCreatedNotifier()
        }
    }

    private fun InProjectScope.storyEventRenamed() {
        provide(StoryEventRenamedReceiver::class) {
            StoryEventRenamedNotifier()
        }
    }

    private fun InProjectScope.storyEventRescheduled() {
        provide(StoryEventRescheduledReceiver::class) {
            StoryEventRescheduledNotifier()
        }
    }

}