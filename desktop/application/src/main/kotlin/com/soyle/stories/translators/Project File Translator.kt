package com.soyle.stories.translators

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.workspace.valueobjects.ProjectFile

fun Project.asProjectFile(location: String) = ProjectFile(id, name.value, location)
fun ProjectFile.asProject() = Project(projectId, NonBlankString.create(projectName)!!)