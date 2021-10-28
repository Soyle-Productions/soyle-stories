package com.soyle.stories.desktop.view.storyevent.timeline.header

import com.soyle.stories.desktop.view.storyevent.item.StoryEventItemMenuComponentDouble
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButtonComponent
import javafx.scene.Node

class TimelineHeaderOptionsButtonComponentDouble(
    val dependencies: StoryEventItemMenuComponent.Dependencies = StoryEventItemMenuComponentDouble.Dependencies()
) : TimelineHeaderOptionsButtonComponent {

    private val gui = object : TimelineHeaderOptionsButtonComponent.Gui,
        StoryEventItemMenuComponent by StoryEventItemMenuComponentDouble(dependencies)
    {}

    override fun TimelineHeaderOptionsButton(selection: TimelineSelectionModel): Node {
        return com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButton(
            selection,
            gui
        )
    }

}