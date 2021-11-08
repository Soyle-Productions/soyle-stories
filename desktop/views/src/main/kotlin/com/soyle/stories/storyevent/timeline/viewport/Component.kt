package com.soyle.stories.storyevent.timeline.viewport

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import javafx.collections.ObservableList
import javafx.event.EventTarget
import tornadofx.add
import tornadofx.observableListOf

@Suppress("FunctionName")
interface TimelineViewPortComponent {

    fun TimelineViewPort(
        storyEventItems: ObservableList<StoryPointLabel> = observableListOf()
    ): TimelineViewPort

    @ViewBuilder
    fun EventTarget.timelineViewPort(
        storyEventItems: ObservableList<StoryPointLabel> = observableListOf(),
        op: TimelineViewPort.() -> Unit = {}
    ): TimelineViewPort = TimelineViewPort(storyEventItems)
            .also { add(it) }
            .apply(op)

    interface Gui : TimelineViewPortGridComponent, TimelineRulerComponent, StoryPointLabelComponent

    interface Dependencies {
        val removeStoryEventController: RemoveStoryEventController
        val adjustStoryEventsTimeController: AdjustStoryEventsTimeController
    }

    companion object {
        fun Implementation(
            gui: Gui,
            dependencies: Dependencies
        ): TimelineViewPortComponent =
            object : TimelineViewPortComponent {
                override fun TimelineViewPort(storyEventItems: ObservableList<StoryPointLabel>): TimelineViewPort {
                    return TimelineViewPort(storyEventItems, gui, dependencies)
                }
            }
    }

}