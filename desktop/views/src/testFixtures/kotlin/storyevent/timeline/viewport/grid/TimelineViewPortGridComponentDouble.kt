package com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewportContext
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx

class TimelineViewPortGridComponentDouble(
    private val gui: TimelineViewPortGridComponent.Gui
) : TimelineViewPortGridComponent by TimelineViewPortGridComponent.Implementation(Dispatchers.Default, Dispatchers.JavaFx, gui) {

    constructor() : this(
        object : TimelineViewPortGridComponent.Gui,
            StoryPointLabelComponent by StoryPointLabelComponentDouble() {}
    )

}