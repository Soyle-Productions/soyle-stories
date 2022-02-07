package com.soyle.stories.repositories

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.stores.FileStore
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.valueobjects.ProjectFile
import java.io.File

class ProjectFileRepository(
    private val fileStore: FileStore<ProjectFile>
) : ProjectRepository, FileRepository {

    private val fileSystem: Map<String, MutableSet<String>> = mapOf(
        "directories" to mutableSetOf(),
        "files" to mutableSetOf()
    )

    override suspend fun createFile(projectFile: ProjectFile) {
        fileSystem.getValue("files").add(projectFile.location)
        fileStore.createFile(projectFile.location, projectFile)
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

    override suspend fun getProjectAtLocation(location: String): Project? = fileStore.getFileAt(location)?.let {
        Project(it.projectId, NonBlankString.create(it.projectName)!!)
    }
}