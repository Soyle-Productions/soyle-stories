package com.soyle.stories.domain.project.changes

import com.soyle.stories.domain.project.Project

data class ProjectRenamed(
    override val projectId: Project.Id,
    val oldName: String,
    val newName: String
) : ProjectChange