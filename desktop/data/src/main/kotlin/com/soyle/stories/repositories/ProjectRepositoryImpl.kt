package com.soyle.stories.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.stores.FileStore
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.valueobjects.ProjectFile

class ProjectRepositoryImpl(
    private val projectLocation: String,
    private val fileStore: FileStore<ProjectFile>
) : ProjectRepository, com.soyle.stories.workspace.repositories.ProjectRepository {

    override suspend fun addNewProject(project: Project) {
        fileStore.createFile(projectLocation, ProjectFile(project.id, project.name, projectLocation))
    }

    override suspend fun getProjectAtLocation(location: String): Project? = fileStore.getFileAt(location)?.let {
        Project(it.projectId, it.projectName)
    }

}