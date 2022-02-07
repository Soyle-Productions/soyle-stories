package com.soyle.stories.domain.project.changes

import com.soyle.stories.domain.project.Project

sealed interface ProjectChange {
    val projectId: Project.Id
}