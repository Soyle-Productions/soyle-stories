package com.soyle.stories.domain.project.changes

import com.soyle.stories.domain.project.Project

data class ProjectStarted(
    override val projectId: Project.Id,
    val name: String
) : ProjectChange