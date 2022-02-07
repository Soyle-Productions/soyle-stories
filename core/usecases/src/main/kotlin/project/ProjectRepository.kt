package com.soyle.stories.usecase.project

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.exceptions.ProjectDoesNotExist
import kotlin.Result.Companion.success

interface ProjectRepository {
    suspend fun addNewProject(project: Project)
    suspend fun getProject(projectId: Project.Id): Project?
}

suspend fun ProjectRepository.getProjectOrError(projectId: Project.Id): Result<Project> =
    getProject(projectId)?.let(::success) ?: Result.failure(ProjectDoesNotExist(projectId))