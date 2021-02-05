package com.soyle.stories.project.startNewProject

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.project.openProject.OpenProjectController
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.repositories.ProjectRepositoryImpl
import com.soyle.stories.stores.ProjectFileStore
import java.io.File

class StartProjectControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val fileStore: ProjectFileStore,
    private val startNewProjectOutput: StartNewProject.OutputPort,
    private val openProjectController: OpenProjectController
) : StartProjectController {

    override fun startProject(directory: String, name: String) {
        val projectLocation = directory + File.separator + name + ".stry"
        val startProjectUseCase = StartNewProjectUseCase(
            ProjectRepositoryImpl(
                projectLocation,
                fileStore
            )
        )
        threadTransformer.async {
            startProjectUseCase.invoke(name, object : StartNewProject.OutputPort {
                override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
                    openProjectController.openProject(projectLocation)
                    startNewProjectOutput.receiveStartNewProjectResponse(response)
                }

                override fun receiveStartNewProjectFailure(failure: Throwable) {
                    startNewProjectOutput.receiveStartNewProjectFailure(failure)
                }
            })
        }

    }

}