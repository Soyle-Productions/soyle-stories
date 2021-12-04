package com.soyle.stories.desktop.view.storyevent.timeline.header

import com.soyle.stories.desktop.view.storyevent.item.StoryEventItemMenuComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponentDouble
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderOptionsButtonComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent

class TimelineHeaderOptionsButtonComponentDouble(
    val dependencies: TimelineHeaderOptionsButtonComponent.Dependencies = Dependencies(),
    val gui: TimelineHeaderOptionsButtonComponent.Gui = object : TimelineHeaderOptionsButtonComponent.Gui,
        StoryEventItemMenuComponent by StoryEventItemMenuComponentDouble(dependencies),
        TimelineRulerLabelMenuComponent by TimelineRulerLabelMenuComponentDouble(dependencies) {}
) : TimelineHeaderOptionsButtonComponent by TimelineHeaderOptionsButtonComponent.Implementation(gui) {

    class Dependencies : TimelineHeaderOptionsButtonComponent.Dependencies,
            StoryEventItemMenuComponent.Dependencies by StoryEventItemMenuComponentDouble.Dependencies()

}