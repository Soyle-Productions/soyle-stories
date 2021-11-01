package com.soyle.stories.domain.time.changes

import com.soyle.stories.domain.time.Timeline

typealias UnSuccessfulTimelineUpdate = UnSuccessful

class UnSuccessful(override val timeline: Timeline) : TimelineUpdate<Nothing>()