package com.soyle.stories.storyevent.timeline.header

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.timelineHeaderArea
import javafx.beans.property.BooleanProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.layout.HBox
import tornadofx.*
import tornadofx.Stylesheet.Companion.header

@Suppress("FunctionName")
interface TimelineHeaderComponent {

    fun TimelineHeader(
        condensedProperty: BooleanProperty,
        selection: TimelineSelectionModel
    ): Node

    @ViewBuilder
    fun EventTarget.timelineHeader(
        condensedProperty: BooleanProperty = booleanProperty(),
        selection: TimelineSelectionModel = TimelineSelectionModel()
    ): Node {
        return TimelineHeader(condensedProperty, selection).also { add(it) }
    }

    interface Gui : TimelineHeaderCreateButtonComponent, TimelineHeaderOptionsButtonComponent

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimelineHeaderComponent {
            override fun TimelineHeader(condensedProperty: BooleanProperty, selection: TimelineSelectionModel): Node =
                timelineHeader(gui, condensedProperty, selection)
        }
    }

}

fun timelineHeader(
    gui: TimelineHeaderComponent.Gui,
    condensedProperty: BooleanProperty = booleanProperty(),
    selection: TimelineSelectionModel = TimelineSelectionModel()
) = HBox().apply {
    /* properties */
    addClass(timelineHeaderArea)
    isPickOnBounds = false
    /* /properties */

    /* children */
    with(gui) {
        timelineHeaderCreateButton()
        timelineHeaderOptionsButton(selection) {
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
