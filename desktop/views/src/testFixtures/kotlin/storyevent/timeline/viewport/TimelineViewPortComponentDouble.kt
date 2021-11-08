package com.soyle.stories.desktop.view.storyevent.timeline.viewport

import com.soyle.stories.desktop.adapter.storyevent.AdjustStoryEventsTimeControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.RemoveStoryEventControllerDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.TimelineViewPortGridComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import javafx.collections.ObservableList
import javafx.collections.ObservableSet

class TimelineViewPortComponentDouble(
    val dependencies: TimelineViewPortComponent.Dependencies = Dependencies(),
    val gui: TimelineViewPortComponent.Gui,
) : TimelineViewPortComponent by TimelineViewPortComponent.Implementation(gui, dependencies) {

    constructor(
        dependencies: TimelineViewPortComponent.Dependencies = Dependencies()
    ) : this(
        dependencies,
        object : TimelineViewPortComponent.Gui,
            TimelineRulerComponent by TimelineViewPortRulerComponentDouble(),
            TimelineViewPortGridComponent by TimelineViewPortGridComponentDouble(),
            StoryPointLabelComponent by StoryPointLabelComponentDouble()
        {}
    )

    class Dependencies(
        override val removeStoryEventController: RemoveStoryEventController = RemoveStoryEventControllerDouble(),
        override val adjustStoryEventsTimeController: AdjustStoryEventsTimeController = AdjustStoryEventsTimeControllerDouble()
    ) : TimelineViewPortComponent.Dependencies

}