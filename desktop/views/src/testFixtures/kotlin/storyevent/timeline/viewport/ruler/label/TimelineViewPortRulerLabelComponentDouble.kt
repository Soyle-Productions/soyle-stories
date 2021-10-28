package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label

import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import javafx.collections.ObservableSet

class TimelineViewPortRulerLabelComponentDouble : TimeSpanLabelComponent {

    override fun TimeSpanLabel(selection: ObservableSet<UnitOfTime>): TimeSpanLabel {
        return com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel(selection)
    }

}