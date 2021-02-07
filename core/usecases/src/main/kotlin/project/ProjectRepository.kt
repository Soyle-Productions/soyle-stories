package com.soyle.stories.usecase.project

import com.soyle.stories.domain.project.Project

interface ProjectRepository {
    suspend fun addNewProject(project: Project)
}