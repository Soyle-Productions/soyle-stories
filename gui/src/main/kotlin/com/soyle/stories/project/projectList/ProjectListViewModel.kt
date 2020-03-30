package com.soyle.stories.project.projectList

import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:01 AM
 */
data class ProjectListViewModel(
    val isSplashScreenVisible: Boolean,
    val isWelcomeScreenVisible: Boolean,
    val openProjectRequest: ProjectFileViewModel?,
    val openProjects: List<ProjectFileViewModel>,
    val failedProjects: List<ProjectIssueViewModel>,
    val closeProjectRequest: ProjectViewModel?
) {
    val isFailedProjectDialogVisible: Boolean
        get() = failedProjects.isNotEmpty()

    val isOpenProjectOptionsDialogOpen: Boolean
        get() = openProjectRequest != null
}

class ProjectViewModel(val projectId: UUID, val name: String)
class ProjectFileViewModel(val projectId: UUID, val name: String, val location: String)
class ProjectIssueViewModel(
    val name: String,
    val location: String,
    val additionalInformation: String = ""
)