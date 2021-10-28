package com.soyle.stories.desktop.view.storyevent.timeline.header

import com.soyle.stories.desktop.view.storyevent.item.StoryEventItemMenuComponentDouble
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderCreateButtonComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButtonComponent
import com.soyle.stories.storyevent.timeline.header.timelineHeader
import javafx.beans.property.BooleanProperty
import javafx.scene.Node

class TimelineHeaderComponentDouble(
    val createButtonDependencies: CreateStoryEventController = TimelineHeaderCreateButtonComponentDouble.Dependencies(),
    val optionsButtonDependencies: StoryEventItemMenuComponent.Dependencies = StoryEventItemMenuComponentDouble.Dependencies(),
) : TimelineHeaderComponent {

    private val gui = object : TimelineHeaderComponent.Gui,
        TimelineHeaderCreateButtonComponent by TimelineHeaderCreateButtonComponentDouble(createButtonDependencies),
            TimelineHeaderOptionsButtonComponent by TimelineHeaderOptionsButtonComponentDouble(optionsButtonDependencies)
    {}

    override fun TimelineHeader(condensedProperty: BooleanProperty, selection: TimelineSelectionModel): Node {
        return timelineHeader(
            gui,
            condensedProperty,
            selection
        )
    }

}