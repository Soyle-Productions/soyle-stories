package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import tornadofx.item

class TimelineRulerLabelMenu(
    selection: TimeRangeSelection = TimelineSelectionModel()
) : ContextMenu() {

    private fun populateItems(selection: Set<TimeRange>) {
        val newItems = if (selection.size == 1) singleRangeSelectionItems(selection.single().duration)
        else itemsForMultiSelection()
        items.setAll(newItems)
    }

    private fun singleRangeSelectionItems(duration: UnitOfTime): List<MenuItem> {
        return listOf(
            MenuItem("Insert ${duration.value} units of time before"),
            MenuItem("Insert ${duration.value} units of time after"),
            MenuItem("Delete ${duration.value} units of time")
        )
    }

    private fun itemsForMultiSelection(): List<MenuItem> {
        return listOf(
            MenuItem("Delete all ranges")
        )
    }

    private val selectionListener = InvalidationListener { populateItems(selection) }
    private val weakSelectionListener = WeakInvalidationListener(selectionListener)

    init {
        selection.addListener(weakSelectionListener)
        populateItems(selection)
    }

}