package com.soyle.stories.storyevent.timeline.viewport.ruler.label

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.TimelineStyles
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.LongProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableSet
import javafx.event.EventTarget
import javafx.scene.control.ContextMenu
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import tornadofx.*

interface TimeSpanLabelComponent {

    fun TimeSpanLabel(selection: TimeRangeSelection, storyPointLabels: List<StoryPointLabel>): TimeSpanLabel

    @ViewBuilder
    fun EventTarget.timeSpanLabel(
        selection: TimeRangeSelection,
        storyPointLabels: List<StoryPointLabel>,
        op: TimeSpanLabel.() -> Unit = {}): TimeSpanLabel = TimeSpanLabel(selection, storyPointLabels)
        .also { add(it) }
        .apply(op)

    interface Gui : TimelineRulerLabelMenuComponent

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimeSpanLabelComponent {
            override fun TimeSpanLabel(selection: TimeRangeSelection, storyPointLabels: List<StoryPointLabel>): TimeSpanLabel {
                return TimeSpanLabel(selection, storyPointLabels, gui)
            }
        }
    }

}

class TimeSpanLabel(
    private val selection: TimeRangeSelection = TimelineSelectionModel(),
    storyPointLabels: List<StoryPointLabel>,

    gui: TimeSpanLabelComponent.Gui
) : Region() {

    private val rangeProperty: ObjectProperty<TimeRange> = objectProperty(TimeRange(0 .. 0L))
    fun range() = rangeProperty
    var range: TimeRange by rangeProperty

    private val truncatedAboveThousand = longBinding(rangeProperty) { get().start.value % 1000 }

    private val label = label {
        textProperty().bind(truncatedAboveThousand.asString("%d"))
    }

    private val contextMenuProperty = objectProperty<TimelineRulerLabelMenu?>(gui.TimelineRulerLabelMenu(selection, storyPointLabels))
    fun contextMenu() = contextMenuProperty
    var contextMenu by contextMenuProperty

    init {
        addClass(TimelineStyles.timeLabel)
        asSurface {
            relativeElevation = Elevation[2]!!
        }
        minHeight = USE_PREF_SIZE
        setOnMousePressed {
            if (! selection.contains(range)) {
                if (it.isShiftDown) selection.extendFromRecentStart(range)
                else if (it.isShortcutDown) selection.add(range)
                else selection.restart(range)
            }
        }
        setOnMouseDragged {
            if (it.isPrimaryButtonDown) {
                it.consume()
                val draggedToNode = it.pickResult?.intersectedNode
                if (draggedToNode is TimeSpanLabel) {
                    selection.extendFromRecentStart(draggedToNode.range)
                }
            }
        }
        setOnDragDetected {
            if (it.isPrimaryButtonDown) {
                it.consume()
                startFullDrag()
            }
        }
        setOnContextMenuRequested { event ->
            if (selection.contains(range)) {
                contextMenu?.run {
                    show(this@TimeSpanLabel, event.screenX, event.screenY)
                    event.consume()
                }
            }
        }
    }

    override fun toString(): String {
        return super.toString()+"\'${label.text}\'" + if (pseudoClassStates.isNotEmpty()) " (${pseudoClassStates})" else ""
    }
}