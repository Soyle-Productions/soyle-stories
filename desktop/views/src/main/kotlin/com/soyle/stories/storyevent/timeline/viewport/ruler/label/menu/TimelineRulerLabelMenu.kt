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
import tornadofx.*

class TimelineRulerLabelMenu(
    private val selection: TimeRangeSelection = TimelineSelectionModel(),
    private val storyPointLabels: ObservableList<StoryPointLabel>,

    private val dependencies: TimelineRulerLabelMenuComponent.Dependencies
) : ContextMenu() {

    private fun insertBefore() {
        val range = selection.get() ?: return
        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
            setOfIdsAfterStart(range),
            range.duration.value
        )
    }

    private fun insertAfter() {
        val range = selection.get() ?: return
        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
            setOfIdsAfterEnd(range),
            range.duration.value
        )
    }

    private fun remove() {
        val range = selection.get() ?: return
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

    init {
        item("") {
            id = "insert-before"
            textProperty().bind(selection.stringBinding {
                "Insert ${it?.duration?.value} unit${if (it?.duration?.value == 1L) "" else "s"} of time before"
            })
            action(::insertBefore)
        }
        item("") {
            id = "insert-after"
            textProperty().bind(selection.stringBinding {
                "Insert ${it?.duration?.value} unit${if (it?.duration?.value == 1L) "" else "s"} of time after"
            })
            action(::insertAfter)
        }
        item("") {
            id = "delete"
            textProperty().bind(selection.stringBinding {
                "Remove ${it?.duration?.value} unit${if (it?.duration?.value == 1L) "" else "s"} of time"
            })
            disableWhen(selectionContainsStoryPoint())
            action(::remove)
        }
    }

    private fun selectionContainsStoryPoint() = booleanBinding(storyPointLabels, selection) {
        val range = selection.get() ?: return@booleanBinding false
        storyPointLabels.binarySubList(
            predicate = { range.range.contains(it.time) },
            lookForward = { it.time < range.range.last }
        ).isNotEmpty()
    }

}