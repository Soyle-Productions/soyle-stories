package com.soyle.stories.desktop.view.storyevent.timeline.viewport

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.timeline.TimelineStyles
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridStyles
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler

class TimelineViewportAccess private constructor(val viewport: TimelineViewPort) : NodeAccess<TimelineViewPort>(viewport) {
    companion object {
        fun TimelineViewPort.access() = TimelineViewportAccess(this)
    }

    val ruler: TimelineRuler by mandatoryChild(TimelineStyles.ruler)

    val grid: TimelineViewPortGrid by mandatoryChild(TimelineViewPortGridStyles.timelineViewPortGrid)
}