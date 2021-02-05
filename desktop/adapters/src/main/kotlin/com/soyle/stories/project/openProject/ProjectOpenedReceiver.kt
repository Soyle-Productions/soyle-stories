package com.soyle.stories.project.openProject

import com.soyle.stories.workspace.usecases.openProject.OpenProject

interface ProjectOpenedReceiver {

    suspend fun receiveOpenedProject(openedProject: OpenProject.ResponseModel)

}