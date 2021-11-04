package com.soyle.stories.storyevent.timeline.viewport.ruler.selection

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.TimeRange
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import tornadofx.*

@Suppress("FunctionName")
fun SelectionRegion(range: TimeRange, scale: Scale): Region {
    return Pane().apply {
        val duration = range.duration
        val widthInPx = scale(duration)
        layoutX = scale(range.start).value
        prefWidth = widthInPx.value
        isPickOnBounds = false
        label {
            isMouseTransparent = true
            style {
                backgroundColor = multi(ColorStyles.lightSelectionColor.deriveColor(0.0, 1.0, 1.0, 0.4))
                borderColor = multi(box(ColorStyles.primaryColor))
                borderInsets = multi(box((-1.0).px))
            }
            prefWidth = widthInPx.value
            maxWidth = Double.MAX_VALUE
            text = " "
        }
        usePrefHeight = true
        polygon(0.0, 0.0, 16.0, 0.0, 8.0, 8.0) {
            isManaged = false
            style {
                fill = ColorStyles.primaryColor
            }
            layoutX = -8.0
            layoutY = -8.0
        }
        polygon(0.0, 0.0, 16.0, 0.0, 8.0, 8.0) {
            isManaged = false
            style {
                fill = ColorStyles.primaryColor
            }
            layoutX = widthInPx.value - 8.0
            layoutY = -8.0
        }
    }
}