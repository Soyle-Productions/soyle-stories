/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 3:42 PM
 */
package com.soyle.stories.project.failedProjectsDialog

import com.soyle.stories.project.projectList.ProjectIssueViewModel

class FailedProjectsDialogViewModel(
    val isVisible: Boolean,
    val failedProjects: List<ProjectIssueViewModel>
)