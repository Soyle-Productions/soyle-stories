package com.soyle.stories.storyevent.timeline.viewport.ruler

import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.UnitOfTime
import javafx.beans.Observable
import javafx.collections.SetChangeListener

interface TimeRangeSelection : Set<TimeRange>, Observable // not using ObservableSet because java Set is mutable.
{
    /**
     * If there is no starting point marked, behaves like [add]
     *
     * Otherwise, removes the most recent added range and adds a new one from the starting point to this [unitOfTime]
     */
    fun extendFromRecentStart(unitOfTime: UnitOfTime)

    /**
     * If there is no starting point marked, behaves like [add]
     *
     * Otherwise, removes the most recent added range and adds a new one that encompasses the [extendToCoverRange] and
     * the most recent range
     */
    fun extendFromRecentStart(extendToCoverRange: TimeRange)

    /**
     * Clears the current selection and adds a range that only covers this starting point
     *
     * Also marks this starting point to extend from
     */
    fun restart(newStart: UnitOfTime)

    /**
     * Clears the current selection and adds only this [newRange]
     *
     * Also marks the start of [newRange] to extend from
     */
    fun restart(newRange: TimeRange)

    /**
     * Adds a range that only covers this starting point
     *
     * Also marks this starting point to extend from
     */
    fun add(nextStart: UnitOfTime)


    /**
     * Adds this [newRange]
     *
     * Also marks the start of [newRange] to extend from
     */
    fun add(newRange: TimeRange)

    /**
     * Add a listener to this observable set.
     * @param listener the listener for listening to the set changes
     */
    fun addListener(listener: SetChangeListener<in TimeRange>)

    /**
     * Tries to removed a listener from this observable set. If the listener is not
     * attached to this list, nothing happens.
     * @param listener a listener to remove
     */
    fun removeListener(listener: SetChangeListener<in TimeRange>)

}