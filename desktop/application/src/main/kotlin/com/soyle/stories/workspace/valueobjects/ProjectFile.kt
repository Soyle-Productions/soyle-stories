/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:42 AM
 */
package com.soyle.stories.workspace.valueobjects

import com.soyle.stories.domain.project.Project


class ProjectFile(
    val projectId: Project.Id,
    val projectName: String,
    val location: String
)