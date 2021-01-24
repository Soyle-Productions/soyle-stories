package com.soyle.stories.stores

import com.soyle.stories.workspace.valueobjects.ProjectFile

class ProjectFileStore : FileStore<ProjectFile> {

    private val fileSystem: Map<String, MutableSet<String>> = mapOf(
        "directories" to mutableSetOf(),
        "files" to mutableSetOf()
    )

    private val projects = mutableMapOf<String, ProjectFile>()

    override suspend fun createFile(location: String, data: ProjectFile) {
        fileSystem.getValue("files").add(location)
        projects[location] = data
    }

    override suspend fun getFileAt(location: String): ProjectFile? = projects[location]

}