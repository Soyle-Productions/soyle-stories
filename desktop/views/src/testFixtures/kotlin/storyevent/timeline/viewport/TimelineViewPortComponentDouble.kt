package com.soyle.stories.desktop.view.storyevent.timeline.viewport

import com.soyle.stories.desktop.adapter.storyevent.RemoveStoryEventControllerDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.TimelineViewPortGridComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import javafx.collections.ObservableList
import javafx.collections.ObservableSet

class TimelineViewPortComponentDouble(
    val dependencies: TimelineViewPortComponent.Dependencies = Dependencies()
) : TimelineViewPortComponent
{

    class Dependencies(
        override val removeStoryEventController: RemoveStoryEventController = RemoveStoryEventControllerDouble()
    ) : TimelineViewPortComponent.Dependencies

    val gui: TimelineViewPortComponent.Gui = object : TimelineViewPortComponent.Gui,
        TimelineRulerComponent by TimelineViewPortRulerComponentDouble(),
        TimelineViewPortGridComponent by TimelineViewPortGridComponentDouble()
    {}

    override fun TimelineViewPort(storyEventItems: ObservableList<StoryPointLabel>): TimelineViewPort {

        return TimelineViewPortComponent.Implementation(gui, dependencies).TimelineViewPort(storyEventItems)
    }

}