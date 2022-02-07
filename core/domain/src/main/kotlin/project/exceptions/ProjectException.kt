package com.soyle.stories.domain.project.exceptions

import com.soyle.stories.domain.project.Project

interface ProjectException {
    val projectId: Project.Id
}