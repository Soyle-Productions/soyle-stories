package com.soyle.stories.workspace.repositories

import com.soyle.stories.workspace.valueobjects.ProjectFile

interface FileRepository {

    suspend fun doesDirectoryExist(directory: String): Boolean
    suspend fun doesFileExist(filePath: String): Boolean
    suspend fun createFile(projectFile: ProjectFile)

}