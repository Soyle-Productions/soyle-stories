package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

import com.soyle.stories.common.collections.binarySubList
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import tornadofx.action
import tornadofx.booleanBinding
import tornadofx.disableWhen
import tornadofx.enableWhen

class TimelineRulerLabelMenu(
    selection: TimeRangeSelection = TimelineSelectionModel(),
    private val storyPointLabels: ObservableList<StoryPointLabel>,

    private val dependencies: TimelineRulerLabelMenuComponent.Dependencies
) : ContextMenu() {

    private fun populateItems(selection: TimeRange?) {
        val newItems = if (selection != null) singleRangeSelectionItems(selection)
        else listOf(MenuItem())
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
                disableWhen(booleanBinding(storyPointLabels) {
                    storyPointLabels.binarySubList(
                        predicate = { range.range.contains(it.time) },
                        lookForward = { it.time < range.range.last }
                    ).isNotEmpty()
                })
                action(remove(range))
            }
        )
    }

    private fun insertBefore(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
            setOfIdsAfterStart(range),
            range.duration.value
        )
    }

    private fun insertAfter(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
            setOfIdsAfterEnd(range),
            range.duration.value
        )
    }

    private fun remove(range: TimeRange): () -> Unit = {
        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
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

    private val selectionListener = InvalidationListener { populateItems(selection.get()) }
    private val weakSelectionListener = WeakInvalidationListener(selectionListener)

    init {
        selection.addListener(weakSelectionListener)
        populateItems(selection.get())
    }

}