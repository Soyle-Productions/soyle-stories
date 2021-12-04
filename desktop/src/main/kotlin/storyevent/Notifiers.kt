package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.coverage.StoryEventCoveredBySceneNotifier
import com.soyle.stories.storyevent.coverage.StoryEventCoveredBySceneReceiver
import com.soyle.stories.storyevent.coverage.StoryEventUncoveredBySceneNotifier
import com.soyle.stories.storyevent.coverage.StoryEventUncoveredBySceneReceiver
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
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
            storyEventNoLongerHappens()
            storyEventCoveredByScene()
            storyEventUncoveredByScene()
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

    private fun InProjectScope.storyEventNoLongerHappens() {
        provide(StoryEventNoLongerHappensReceiver::class) {
            StoryEventNoLongerHappensNotifier()
        }
    }

    private fun InProjectScope.storyEventCoveredByScene() {
        provide(StoryEventCoveredBySceneReceiver::class) {
            StoryEventCoveredBySceneNotifier()
        }
    }

    private fun InProjectScope.storyEventUncoveredByScene() {
        provide(StoryEventUncoveredBySceneReceiver::class) {
            StoryEventUncoveredBySceneNotifier()
        }
    }

}