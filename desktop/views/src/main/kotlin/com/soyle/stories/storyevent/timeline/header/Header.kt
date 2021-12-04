package com.soyle.stories.storyevent.timeline.header

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.timelineHeaderArea
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.layout.HBox
import tornadofx.*

@Suppress("FunctionName")
interface TimelineHeaderComponent {

    fun TimelineHeader(
        condensedProperty: BooleanProperty,
        selection: TimelineSelectionModel,
        storyPointLabels: ObservableList<StoryPointLabel>
    ): Node

    @ViewBuilder
    fun EventTarget.timelineHeader(
        condensedProperty: BooleanProperty = booleanProperty(),
        selection: TimelineSelectionModel = TimelineSelectionModel(),
        storyPointLabels: ObservableList<StoryPointLabel>
    ): Node {
        return TimelineHeader(condensedProperty, selection, storyPointLabels).also { add(it) }
    }

    interface Gui : TimelineHeaderCreateButtonComponent, TimelineHeaderOptionsButtonComponent

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimelineHeaderComponent {
            override fun TimelineHeader(
                condensedProperty: BooleanProperty, selection: TimelineSelectionModel,
                storyPointLabels: ObservableList<StoryPointLabel>
            ): Node =
                timelineHeader(gui, condensedProperty, selection, storyPointLabels)
        }
    }

}

fun timelineHeader(
    gui: TimelineHeaderComponent.Gui,
    condensedProperty: BooleanProperty = booleanProperty(),
    selection: TimelineSelectionModel = TimelineSelectionModel(),
    storyPointLabels: ObservableList<StoryPointLabel> = observableListOf(),
) = HBox().apply {
    /* properties */
    addClass(timelineHeaderArea)
    isPickOnBounds = false
    /* /properties */

    /* children */
    with(gui) {
        timelineHeaderCreateButton()
        timelineHeaderOptionsButton(selection, storyPointLabels) {
            disableWhen(selection.empty())
        }
        spacer()
        checkbox("Condensed") {
            isSelected = false
            condensedProperty.bind(selectedProperty())
        }
    }
    /* /children */
}
