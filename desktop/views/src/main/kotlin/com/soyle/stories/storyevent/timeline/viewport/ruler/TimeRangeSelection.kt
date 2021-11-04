package com.soyle.stories.storyevent.timeline.viewport.ruler

import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.UnitOfTime
import javafx.beans.value.ObservableObjectValue

interface TimeRangeSelection : ObservableObjectValue<TimeRange?>
{
    /**
     * Removes the most recent added range and adds a new one from the starting point to this [unitOfTime]
     */
    fun extendFromRecentStart(unitOfTime: UnitOfTime)

    /**
     * Removes the most recent added range and adds a new one that encompasses the [extendToCoverRange] and
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

}