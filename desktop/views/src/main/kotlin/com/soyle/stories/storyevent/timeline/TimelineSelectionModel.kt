package com.soyle.stories.storyevent.timeline

import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.WeakInvalidationListener
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import tornadofx.booleanBinding
import tornadofx.observableSetOf
import kotlin.math.max
import kotlin.math.min

class TimelineSelectionModel private constructor(private val timeRangeSelection: TimeRangeSelectionImpl) :
    TimeRangeSelection by timeRangeSelection {

    constructor() : this(TimeRangeSelectionImpl())

    val storyEvents = StoryEventItemSelection()

    private class TimeRangeSelectionImpl private constructor(private val backingSet: ObservableSet<TimeRange>) :
        TimeRangeSelection, Set<TimeRange> by backingSet, Observable by backingSet {
        constructor() : this(observableSetOf())

        private var rangeToExtend: TimeRange? = null
        private var recentRange: TimeRange? = null

        override fun add(nextStart: UnitOfTime) {
            val newRange = nextStart..nextStart + 1
            add(newRange)
        }

        override fun add(newRange: TimeRange) {
            recentRange = newRange
            rangeToExtend = newRange
            backingSet.add(newRange)
        }

        override fun contains(element: TimeRange): Boolean {
            return backingSet.contains(element) || backingSet.any {
                it.hasOverlapWith(element)
            }
        }

        override fun addListener(listener: SetChangeListener<in TimeRange>) = backingSet.addListener(listener)

        override fun removeListener(listener: SetChangeListener<in TimeRange>) = backingSet.removeListener(listener)

        override fun restart(newRange: TimeRange) {
            backingSet.clear()
            add(newRange)
        }

        override fun extendFromRecentStart(unitOfTime: UnitOfTime) {
            val lastRange = recentRange
            if (lastRange == null) {
                val newRange = unitOfTime..unitOfTime + 1
                add(newRange)
            } else {
                backingSet.remove(lastRange)
                val newRange = if (unitOfTime > lastRange.start) lastRange.start..unitOfTime + 1
                else unitOfTime..lastRange.start + 1
                add(newRange)
            }
        }

        override fun extendFromRecentStart(extendToCoverRange: TimeRange) {
            val extensionRange = rangeToExtend
            val lastRange = recentRange
            if (lastRange == null || extensionRange == null) {
                add(extendToCoverRange)
            } else {
                backingSet.remove(lastRange)
                val newStart = min(extendToCoverRange.start.value, extensionRange.start.value)
                val newEnd = max(extendToCoverRange.endInclusive.value, extensionRange.endInclusive.value)
                val newRange = TimeRange(newStart .. newEnd)
                recentRange = newRange
                backingSet.add(newRange)
            }
        }

        override fun restart(newStart: UnitOfTime) {
            val newRange = newStart..newStart + 1
            restart(newRange)
        }

        fun clear() = backingSet.clear()
    }

    val timeRanges: TimeRangeSelection
        get() = this

    private val emptyProperty = storyEvents.empty().and(booleanBinding(timeRanges) { timeRanges.isEmpty() })
    fun empty() = emptyProperty

    override fun add(newRange: TimeRange) {
        storyEvents.clear()
        timeRangeSelection.add(newRange)
    }

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

    override fun add(nextStart: UnitOfTime) {
        storyEvents.clear()
        timeRangeSelection.add(nextStart)
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