package com.soyle.stories.usecase.project.exceptions

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.EntityNotFoundException

data class ProjectNotFoundException(val projectId: Project.Id, override val message: String?) : EntityNotFoundException(projectId.uuid), ProjectException

fun ProjectDoesNotExist(projectId: Project.Id) = ProjectNotFoundException(projectId, "$projectId does not exist")