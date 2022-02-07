package com.soyle.stories.storyevent.details

import com.soyle.stories.di.resolve
import com.soyle.stories.domain.storyevent.StoryEvent
import javafx.scene.Parent
import tornadofx.View
import tornadofx.objectProperty

class StoryEventDetailsComponent : View() {

    val storyEventId = objectProperty<StoryEvent.Id?>(null)

    private val dependencies = runCatching { resolve<StoryEventDetailsDependencies>() }.run {
        exceptionOrNull()?.printStackTrace()
        getOrNull()
    }

    override val root: Parent = StoryEventDetails(
        StoryEventDetailsViewModel(
            storyEventId,
            dependencies
        )
    )

}