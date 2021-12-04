package com.soyle.stories.desktop.view.storyevent.timeline.header

import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderCreateButtonComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButtonComponent

class TimelineHeaderComponentDouble(
    val createButtonDependencies: CreateStoryEventController = TimelineHeaderCreateButtonComponentDouble.Dependencies(),
    val optionsButtonDependencies: TimelineHeaderOptionsButtonComponent.Dependencies =
        TimelineHeaderOptionsButtonComponentDouble.Dependencies(),
    val gui: TimelineHeaderComponent.Gui = object : TimelineHeaderComponent.Gui,
        TimelineHeaderCreateButtonComponent by TimelineHeaderCreateButtonComponentDouble(createButtonDependencies),
        TimelineHeaderOptionsButtonComponent by TimelineHeaderOptionsButtonComponentDouble(optionsButtonDependencies) {}
) : TimelineHeaderComponent by TimelineHeaderComponent.Implementation(gui)