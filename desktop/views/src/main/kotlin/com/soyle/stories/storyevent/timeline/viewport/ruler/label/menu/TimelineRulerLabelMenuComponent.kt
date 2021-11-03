package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection

@Suppress("FunctionName")
interface TimelineRulerLabelMenuComponent {

    fun TimelineRulerLabelMenu(
        selection: TimeRangeSelection,
        storyPointLabels: List<StoryPointLabel>
    ): TimelineRulerLabelMenu

    interface Dependencies {
        val adjustStoryEventsTimeController: AdjustStoryEventsTimeController
    }

    companion object {
        fun Implementation(
            dependencies: Dependencies
        ): TimelineRulerLabelMenuComponent = object : TimelineRulerLabelMenuComponent {
            override fun TimelineRulerLabelMenu(
                selection: TimeRangeSelection,
                storyPointLabels: List<StoryPointLabel>
            ): TimelineRulerLabelMenu {
                return TimelineRulerLabelMenu(selection, storyPointLabels, dependencies)
            }
        }
    }

}