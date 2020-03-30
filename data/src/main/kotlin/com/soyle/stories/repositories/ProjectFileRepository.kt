package com.soyle.stories.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.valueobjects.ProjectFile
import java.io.File

class ProjectFileRepository : ProjectRepository, com.soyle.stories.project.repositories.ProjectRepository, FileRepository {

    companion object {
        private var singleton: ProjectFileRepository? = null
            set(value) {
                if (field != null) error("only one project file repo allowed per application")
                field = value
            }
    }

    init {
        singleton = this
    }

    private val fileSystem: Map<String, MutableSet<String>> = mapOf(
        "directories" to mutableSetOf(),
        "files" to mutableSetOf()
    )

    private val projects = mutableMapOf<String, Project>()

    override suspend fun createFile(projectFile: ProjectFile) {
        fileSystem.getValue("files").add(projectFile.location)
        projects[projectFile.location] = projectFile.run { Project(projectId, projectName) }
    }

    override suspend fun doesDirectoryExist(directory: String): Boolean {
        return File(directory).run {
            isDirectory && exists()
        } || fileSystem.getValue("directories").contains(directory)
    }

    override suspend fun doesFileExist(filePath: String): Boolean {
        return File(filePath).run {
            isFile && exists()
        } || fileSystem.getValue("files").contains(filePath)
    }

    override suspend fun addNewProject(project: Project) {}

    override suspend fun getProjectAtLocation(location: String): Project? = projects[location]
}