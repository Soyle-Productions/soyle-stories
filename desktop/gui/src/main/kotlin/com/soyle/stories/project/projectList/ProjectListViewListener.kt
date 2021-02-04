package com.soyle.stories.project.projectList

import java.net.URI
import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:43 PM
 */
interface ProjectListViewListener {

    suspend fun startApplicationWithParameters(parameters: List<String>)
    suspend fun requestCloseProject(projectId: UUID)
    suspend fun closeProject(projectId: UUID)

    suspend fun ignoreFailure(failingURI: URI)

    suspend fun startNewProject(directory: String, name: String)

    fun openProject(location: String)
    fun replaceCurrentProject(location: String)
    fun forceOpenProject(location: String)

}