package com.soyle.stories.storyevent.timeline

import com.soyle.stories.storyevent.item.StoryEventItemSelection
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import tornadofx.booleanBinding
import tornadofx.observableSetOf

class TimelineSelectionModel {

    val storyEvents = StoryEventItemSelection()

    val timeUnits = observableSetOf<UnitOfTime>()

    private val emptyProperty = storyEvents.empty().and(booleanBinding(timeUnits) { timeUnits.isEmpty() })
    fun empty() = emptyProperty

    fun add(unitOfTime: UnitOfTime) {
        storyEvents.clear()
        timeUnits.add(unitOfTime)
    }

    private val storyEventsInvalidationListener = InvalidationListener {
        if (storyEvents.selectedIds.isNotEmpty()) timeUnits.clear()
    }
    private val weakStoryEventsInvalidationListener = WeakInvalidationListener(storyEventsInvalidationListener)

    init {
        storyEvents.addListener(weakStoryEventsInvalidationListener)
    }

}