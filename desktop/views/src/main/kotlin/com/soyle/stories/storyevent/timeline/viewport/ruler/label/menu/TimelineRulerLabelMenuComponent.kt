package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection

@Suppress("FunctionName")
interface TimelineRulerLabelMenuComponent {

    fun TimelineRulerLabelMenu(
        selection: TimeRangeSelection
    ): TimelineRulerLabelMenu

    companion object {
        fun Implementation(): TimelineRulerLabelMenuComponent = object : TimelineRulerLabelMenuComponent {
            override fun TimelineRulerLabelMenu(selection: TimeRangeSelection): TimelineRulerLabelMenu {
                return com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu(selection)
            }
        }
    }

}