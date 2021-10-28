package com.soyle.stories.storyevent.timeline.header

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import javafx.beans.InvalidationListener
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.MenuButton
import tornadofx.add
import tornadofx.addClass
import tornadofx.bind

@Suppress("FunctionName")
interface TimelineHeaderOptionsButtonComponent {

    fun TimelineHeaderOptionsButton(selection: TimelineSelectionModel): Node

    @ViewBuilder
    fun EventTarget.timelineHeaderOptionsButton(selection: TimelineSelectionModel, op: Node.() -> Unit = {}): Node =
        TimelineHeaderOptionsButton(selection).also { add(it) }.apply(op)

    interface Gui : StoryEventItemMenuComponent

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimelineHeaderOptionsButtonComponent {
            override fun TimelineHeaderOptionsButton(selection: TimelineSelectionModel): Node =
                TimelineHeaderOptionsButton(selection, gui)
        }
    }

}

class TimelineHeaderOptionsButton(
    selection: TimelineSelectionModel = TimelineSelectionModel(),
    gui: TimelineHeaderOptionsButtonComponent.Gui
) : MenuButton("Options") {

    private val storyEventItems = gui.StoryEventItemMenu(selection.storyEvents).items

    init {
        items.setAll(storyEventItems)
    }

    init {
        addClass(secondary, outlined)

    }
}