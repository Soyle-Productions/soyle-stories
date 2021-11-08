package com.soyle.stories.storyevent.timeline.viewport

import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.ObjectExpression
import javafx.collections.ObservableList
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.observableListOf

interface TimelineViewportContext {

    fun visibleRange(): ObjectExpression<TimeRange> = objectProperty(0L.unit .. 1L.unit)
    fun scale(): ObjectExpression<Scale> = objectProperty(Scale.maxZoomIn())
    fun offsetX(): ObjectExpression<Pixels> = objectProperty(Pixels(0.0))
    fun offsetY(): ObjectExpression<Pixels> = objectProperty(Pixels(0.0))
    fun labelsCollapsed(): BooleanExpression = booleanProperty(false)
    fun dragLabels(): ObjectExpression<List<StoryPointLabel>> = objectProperty(listOf())
    val storyPointLabels: ObservableList<StoryPointLabel> get() = observableListOf()
    val selection: TimeRangeSelection get() = TimelineSelectionModel()

}