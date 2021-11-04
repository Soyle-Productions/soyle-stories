package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.TimelineViewPortRulerLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponentDouble
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import javafx.collections.ObservableSet

class TimelineViewPortRulerComponentDouble(
    val gui: TimelineRulerComponent.Gui
) : TimelineRulerComponent by TimelineRulerComponent.Implementation(gui) {

    constructor() : this(object : TimelineRulerComponent.Gui,
        TimeSpanLabelComponent by TimelineViewPortRulerLabelComponentDouble(),
        TimelineRulerLabelMenuComponent by TimelineRulerLabelMenuComponentDouble() {})
}