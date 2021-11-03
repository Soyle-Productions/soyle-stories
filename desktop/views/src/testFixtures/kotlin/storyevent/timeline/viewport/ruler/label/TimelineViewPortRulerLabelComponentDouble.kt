package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponentDouble
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import javafx.collections.ObservableSet

class TimelineViewPortRulerLabelComponentDouble : TimeSpanLabelComponent {

    private val gui = object : TimeSpanLabelComponent.Gui,
        TimelineRulerLabelMenuComponent by TimelineRulerLabelMenuComponentDouble() {}

    override fun TimeSpanLabel(selection: TimeRangeSelection, storyPointLabels: List<StoryPointLabel>): TimeSpanLabel {
        return TimeSpanLabel(selection, storyPointLabels, gui)
    }

}