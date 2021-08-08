package com.soyle.stories.usecase.project

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.framework.CrossDomainTest
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.usecase.project.startNewProject.StartNewProjectUseCase
import kotlinx.coroutines.runBlocking

fun CrossDomainTest.`given a project has been started`(named: String = "Untitled Project"): Project
{
    val useCase = StartNewProjectUseCase(projectRepository)
    var resultingProject: Project? = null
    val output = object : StartNewProject.OutputPort {
        override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
            resultingProject = Project(Project.Id(response.projectId), NonBlankString.create(response.projectName)!!)
        }
        override fun receiveStartNewProjectFailure(failure: Throwable) = throw failure
    }

    runBlocking { useCase.invoke(NonBlankString.create(named)!!, output) }
    return resultingProject!!
}