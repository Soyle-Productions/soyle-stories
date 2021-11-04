package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponentDouble
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import javafx.collections.ObservableSet

class TimelineViewPortRulerLabelComponentDouble(
    val gui: TimeSpanLabelComponent.Gui
) : TimeSpanLabelComponent by TimeSpanLabelComponent.Implementation(gui) {

    constructor() : this(object : TimeSpanLabelComponent.Gui,
        TimelineRulerLabelMenuComponent by TimelineRulerLabelMenuComponentDouble() {}
    )

}