package com.soyle.stories.core.framework

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.usecase.project.startNewProject.StartNewProjectUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

interface `Project Steps` {

    interface Given {

        fun `a project`(named: String = "Untitled"): ExistenceExpectations
        interface ExistenceExpectations {
            fun `has been started`(): Project.Id
        }
    }

}