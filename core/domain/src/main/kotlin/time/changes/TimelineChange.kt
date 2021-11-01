package com.soyle.stories.domain.time.changes

import com.soyle.stories.domain.project.Project

abstract class TimelineChange {
    abstract val projectId: Project.Id
}