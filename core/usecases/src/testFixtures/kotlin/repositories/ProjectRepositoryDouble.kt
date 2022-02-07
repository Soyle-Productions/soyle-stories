package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.ProjectRepository

class ProjectRepositoryDouble(
    private val onAddProject: (Project) -> Unit ={}
) : ProjectRepository {

    val projects = mutableMapOf<Project.Id, Project>()

    fun givenProject(project: Project) {
        projects[project.id] = project
    }

    override suspend fun addNewProject(project: Project) {
        onAddProject(project)
        projects[project.id] = project
    }

    override suspend fun getProject(projectId: Project.Id): Project? {
        return projects[projectId]
    }

}