package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import tornadofx.action

class TimelineRulerLabelMenu(
    selection: TimeRangeSelection = TimelineSelectionModel(),
    private val storyPointLabels: List<StoryPointLabel>,

    private val dependencies: TimelineRulerLabelMenuComponent.Dependencies
) : ContextMenu() {

    private fun populateItems(selection: Set<TimeRange>) {
        val newItems = if (selection.size == 1) singleRangeSelectionItems(selection.single())
        else itemsForMultiSelection()
        items.setAll(newItems)
    }

    private fun singleRangeSelectionItems(range: TimeRange): List<MenuItem> {
        return listOf(
            MenuItem().apply {
                id = "insert-before"
                text =
                    "Insert ${range.duration.value} unit${if (range.duration.value == 1L) "" else "s"} of time before"
                action(insertBefore(range))
            },
            MenuItem().apply {
                id = "insert-after"
                text = "Insert ${range.duration.value} unit${if (range.duration.value == 1L) "" else "s"} of time after"
                action(insertAfter(range))
            },
            MenuItem().apply {
                id = "delete"
                text = "Remove ${range.duration.value} unit${if (range.duration.value == 1L) "" else "s"} of time"
                action(remove(range))
            }
        )
    }

    private fun itemsForMultiSelection(): List<MenuItem> {
        return listOf(
            MenuItem("Remove all ranges").apply {
                id = "delete"
            }
        )
    }

    private fun insertBefore(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
            setOfIdsAfterStart(range),
            range.duration.value
        )
    }

    private fun insertAfter(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
            setOfIdsAfterEnd(range),
            range.duration.value
        )
    }

    private fun remove(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
            setOfIdsAfterStart(range),
            -range.duration.value
        )
    }

    private fun setOfIdsAfterStart(range: TimeRange) = setOfIdsMatching { it.time >= range.start.value }
    private fun setOfIdsAfterEnd(range: TimeRange) = setOfIdsMatching { it.time >= range.endInclusive.value }

    private fun setOfIdsMatching(predicate: (StoryPointLabel) -> Boolean) = storyPointLabels.asSequence()
        .filter(predicate)
        .map { it.storyEventId }
        .toSet()

    private val selectionListener = InvalidationListener { populateItems(selection) }
    private val weakSelectionListener = WeakInvalidationListener(selectionListener)

    init {
        selection.addListener(weakSelectionListener)
        populateItems(selection)
    }

}