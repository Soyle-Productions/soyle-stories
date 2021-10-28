package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.TimelineViewPortRulerLabelComponentDouble
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import javafx.collections.ObservableSet

class TimelineViewPortRulerComponentDouble :
    TimelineRulerComponent
{

    val gui : TimelineRulerComponent.Gui = object : TimelineRulerComponent.Gui,
        TimeSpanLabelComponent by TimelineViewPortRulerLabelComponentDouble()
    {}

    override fun TimelineRuler(selection: ObservableSet<UnitOfTime>): TimelineRuler {

        return TimelineRulerComponent.Implementation(gui).TimelineRuler(selection)
    }

}