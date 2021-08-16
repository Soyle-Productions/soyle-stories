package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.desktop.config.scene.Persistence

object StoryEvents {

    operator fun invoke() {
        UseCases
        Notifiers
    }
}