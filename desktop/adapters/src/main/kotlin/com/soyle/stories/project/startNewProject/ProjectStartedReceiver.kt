package com.soyle.stories.project.startNewProject

import com.soyle.stories.usecase.project.startNewProject.StartNewProject

interface ProjectStartedReceiver {

    suspend fun receiveProjectStarted(projectStarted: StartNewProject.ResponseModel)

}