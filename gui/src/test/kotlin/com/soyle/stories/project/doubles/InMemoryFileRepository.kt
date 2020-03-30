package com.soyle.stories.project.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.valueobjects.ProjectFile

class InMemoryFileRepository : FileRepository, ProjectRepository, com.soyle.stories.project.repositories.ProjectRepository {

    val directories = mutableSetOf<String>()
    val files = mutableSetOf<String>()
    val projects = mutableMapOf<String, Project>()

    override suspend fun createFile(projectFile: ProjectFile) {
        files += projectFile.location
        projects += (projectFile.location to projectFile.let { Project(it.projectId, it.projectName) })
    }

    override suspend fun doesDirectoryExist(directory: String): Boolean = directories.contains(directory)

    override suspend fun doesFileExist(filePath: String): Boolean {
        return files.contains(filePath).also {
            //println("doesFileExist($filePath) -> $it")
        }
    }

    override suspend fun getProjectAtLocation(location: String): Project? {
        return projects[location]
    }

    override suspend fun addNewProject(project: Project) {
    }
}