package com.soyle.stories.storyevent.timeline.viewport.grid

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.event.EventTarget
import tornadofx.add
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
interface TimelineViewPortGridComponent {
    fun TimelineViewPortGrid(): TimelineViewPortGrid

    @ViewBuilder
    fun EventTarget.timelineViewPortGrid(op: TimelineViewPortGrid.() -> Unit = {}): TimelineViewPortGrid = TimelineViewPortGrid()
        .also { add(it) }
        .apply(op)

    companion object {
        fun Implementation(
            asyncContext: CoroutineContext,
            guiContext: CoroutineContext,
            gui: Gui
        ): TimelineViewPortGridComponent = object : TimelineViewPortGridComponent {
            override fun TimelineViewPortGrid(): TimelineViewPortGrid = TimelineViewPortGrid(
                asyncContext, guiContext, gui
            )
        }
    }

    interface Gui : StoryPointLabelComponent

}