/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 11:42 AM
 */
package com.soyle.stories.translators

import com.soyle.stories.entities.Project
import com.soyle.stories.workspace.valueobjects.ProjectFile

fun Project.asProjectFile(location: String) = ProjectFile(id, name, location)
fun ProjectFile.asProject() = Project(projectId, projectName)