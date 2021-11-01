package com.soyle.stories.domain.time.changes

import com.soyle.stories.domain.time.Timeline

sealed class TimelineUpdate<out E : TimelineChange>() {

    abstract val timeline: Timeline
    operator fun component1() = timeline
}