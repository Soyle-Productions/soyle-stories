package com.soyle.stories.domain.time.changes

import com.soyle.stories.domain.time.Timeline

typealias SuccessfulTimelineUpdate<E> = Successful<E>

class Successful<E : TimelineChange>(
    override val timeline: Timeline,
    val change: E
) : TimelineUpdate<E>() {
    operator fun component2() = change
}