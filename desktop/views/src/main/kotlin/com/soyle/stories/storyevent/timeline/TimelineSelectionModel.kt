package com.soyle.stories.storyevent.timeline

import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import tornadofx.booleanBinding
import tornadofx.objectProperty
import tornadofx.observableSetOf
import kotlin.math.max
import kotlin.math.min

class TimelineSelectionModel private constructor(private val timeRangeSelection: TimeRangeSelectionImpl) :
    TimeRangeSelection by timeRangeSelection {

    constructor() : this(TimeRangeSelectionImpl())

    val storyEvents = StoryEventItemSelection()

    private class TimeRangeSelectionImpl private constructor(private val writeableProperty: ObjectProperty<TimeRange?>) :
        TimeRangeSelection, ObservableObjectValue<TimeRange?> by writeableProperty {
        constructor() : this(objectProperty())

        private var startRange: TimeRange? = null

        override fun restart(newRange: TimeRange) {
            startRange = newRange
            writeableProperty.set(newRange)
        }

        override fun extendFromRecentStart(unitOfTime: UnitOfTime) {
            extendFromRecentStart(unitOfTime..unitOfTime + 1)
        }

        override fun extendFromRecentStart(extendToCoverRange: TimeRange) {
            val startRange = startRange ?: return restart(extendToCoverRange)
            writeableProperty.set(
                TimeRange(
                    min(
                        startRange.start.value,
                        extendToCoverRange.start.value
                    )..max(startRange.endInclusive.value, extendToCoverRange.endInclusive.value)
                )
            )
        }

        override fun restart(newStart: UnitOfTime) {
            val newRange = newStart..newStart + 1
            restart(newRange)
        }

        fun clear() {
            startRange = null
            writeableProperty.set(null)
        }
    }

    val timeRange: TimeRangeSelection
        get() = this

    private val emptyProperty = storyEvents.empty().and(booleanBinding(timeRange) { timeRange.value == null })
    fun empty() = emptyProperty

    override fun extendFromRecentStart(unitOfTime: UnitOfTime) {
        storyEvents.clear()
        timeRangeSelection.extendFromRecentStart(unitOfTime)
    }

    override fun restart(newStart: UnitOfTime) {
        storyEvents.clear()
        timeRangeSelection.restart(newStart)
    }

    override fun restart(newRange: TimeRange) {
        storyEvents.clear()
        timeRangeSelection.restart(newRange)
    }

    fun clear() {
        storyEvents.clear()
        timeRangeSelection.clear()
    }

    private val storyEventsInvalidationListener = InvalidationListener {
        if (storyEvents.selectedIds.isNotEmpty()) timeRangeSelection.clear()
    }
    private val weakStoryEventsInvalidationListener = WeakInvalidationListener(storyEventsInvalidationListener)

    init {
        storyEvents.addListener(weakStoryEventsInvalidationListener)
    }

}