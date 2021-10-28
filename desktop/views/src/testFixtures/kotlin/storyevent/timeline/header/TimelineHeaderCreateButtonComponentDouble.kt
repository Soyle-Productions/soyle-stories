package com.soyle.stories.desktop.view.storyevent.timeline.header

import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderCreateButtonComponent
import javafx.scene.Node

class TimelineHeaderCreateButtonComponentDouble(
    val dependencies: CreateStoryEventController = Dependencies()
) : TimelineHeaderCreateButtonComponent {

    class Dependencies : CreateStoryEventController by CreateStoryEventControllerDouble()

    override fun TimelineHeaderCreateButton(): Node {
        return TimelineHeaderCreateButtonComponent.Implementation(dependencies).TimelineHeaderCreateButton()
    }

}