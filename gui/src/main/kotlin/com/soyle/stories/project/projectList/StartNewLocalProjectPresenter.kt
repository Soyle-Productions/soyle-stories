package com.soyle.stories.project.projectList

import com.soyle.stories.project.LocalProjectException
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

internal class StartNewLocalProjectPresenter(
    private val openProjectOutputPort: OpenProject.OutputPort,
    private val closeProjectOutputPort: CloseProject.OutputPort
) : StartNewLocalProject.OutputPort {

    override fun receiveOpenProjectFailure(failure: ProjectException) {
        openProjectOutputPort.receiveOpenProjectFailure(failure)
    }

    override fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        openProjectOutputPort.receiveOpenProjectResponse(response)
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        closeProjectOutputPort.receiveCloseProjectFailure(failure)
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closeProjectOutputPort.receiveCloseProjectResponse(response)
    }

    override fun receiveStartNewLocalProjectFailure(exception: LocalProjectException) {
        println(exception)
    }

}