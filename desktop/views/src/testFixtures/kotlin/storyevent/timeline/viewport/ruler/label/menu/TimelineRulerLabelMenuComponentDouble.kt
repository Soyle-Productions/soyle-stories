package com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent

class TimelineRulerLabelMenuComponentDouble : TimelineRulerLabelMenuComponent {

    override fun TimelineRulerLabelMenu(selection: TimeRangeSelection): TimelineRulerLabelMenu {
        return TimelineRulerLabelMenuComponent.Implementation().TimelineRulerLabelMenu(selection)
    }
}