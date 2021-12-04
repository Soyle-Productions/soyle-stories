package com.soyle.stories.storyevent.timeline.header

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.MenuButton
import tornadofx.*

@Suppress("FunctionName")
interface TimelineHeaderOptionsButtonComponent {

    fun TimelineHeaderOptionsButton(
        selection: TimelineSelectionModel,
        storyPointLabels: ObservableList<StoryPointLabel>,
    ): Node

    @ViewBuilder
    fun EventTarget.timelineHeaderOptionsButton(
        selection: TimelineSelectionModel,
        storyPointLabels: ObservableList<StoryPointLabel>, op: Node.() -> Unit = {}
    ): Node =
        TimelineHeaderOptionsButton(selection, storyPointLabels).also { add(it) }.apply(op)

    interface Gui : StoryEventItemMenuComponent, TimelineRulerLabelMenuComponent
    interface Dependencies : StoryEventItemMenuComponent.Dependencies, TimelineRulerLabelMenuComponent.Dependencies

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimelineHeaderOptionsButtonComponent {
            override fun TimelineHeaderOptionsButton(
                selection: TimelineSelectionModel,
                storyPointLabels: ObservableList<StoryPointLabel>
            ): Node =
                TimelineHeaderOptionsButton(selection, storyPointLabels, gui)
        }
    }

}

class TimelineHeaderOptionsButton(
    selection: TimelineSelectionModel = TimelineSelectionModel(),
    storyPointLabels: ObservableList<StoryPointLabel> = observableListOf(),
    gui: TimelineHeaderOptionsButtonComponent.Gui
) : MenuButton("Options") {

    private val storyEventItems = gui.StoryEventItemMenu(selection.storyEvents).items
    private val timeRangeItems = gui.TimelineRulerLabelMenu(selection, storyPointLabels).items
    private val itemsListener = InvalidationListener {
        when {
            selection.storyEvents.empty().get() && selection.timeRange.get() == null -> {
                items.clear()
            }
            selection.storyEvents.empty().get() -> {
                items.setAll(timeRangeItems)
            }
            else -> {
                items.setAll(storyEventItems)
            }
        }
    }
    private val weak_itemsListener = WeakInvalidationListener(itemsListener)

    init {
        selection.storyEvents.empty().addListener(weak_itemsListener)
        selection.timeRange.addListener(weak_itemsListener)
        itemsListener.invalidated(selection)
    }

    init {
        addClass(secondary, outlined)

    }
}