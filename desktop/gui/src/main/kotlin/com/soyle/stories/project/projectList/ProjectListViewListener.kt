package com.soyle.stories.project.projectList

import com.soyle.stories.domain.validation.NonBlankString
import java.net.URI
import java.util.*

interface ProjectListViewListener {

    suspend fun startApplicationWithParameters(parameters: List<String>)
    suspend fun requestCloseProject(projectId: UUID)
    suspend fun closeProject(projectId: UUID)

    suspend fun ignoreFailure(failingURI: URI)

    suspend fun startNewProject(directory: String, name: NonBlankString)

    fun openProject(location: String)
    fun replaceCurrentProject(location: String)
    fun forceOpenProject(location: String)

}