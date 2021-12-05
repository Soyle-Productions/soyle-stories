package com.soyle.stories.core

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.usecase.project.startNewProject.StartNewProjectUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

object `Project Steps` {

    interface Given {

        val projectRepository: ProjectRepository

        interface ExistenceExpectations {
            fun `has been started`(): Project.Id
        }
        fun `a project`(named: String = "Untitled"): ExistenceExpectations = object : ExistenceExpectations {
            override fun `has been started`(): Project.Id {
                val deferred = CompletableDeferred<Project.Id>()
                runBlocking {
                    StartNewProjectUseCase(projectRepository).invoke(nonBlankStr(named), object : StartNewProject.OutputPort {
                        override fun receiveStartNewProjectFailure(failure: Throwable) {
                            throw failure
                        }

                        override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
                            deferred.complete(Project.Id(response.projectId))
                        }
                    })
                }
                return runBlocking { deferred.await() }
            }
        }

    }

}