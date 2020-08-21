package com.soyle.stories.project.startNewProject

import com.soyle.stories.project.usecases.startNewProject.StartNewProject

interface ProjectStartedReceiver {

    suspend fun receiveProjectStarted(projectStarted: StartNewProject.ResponseModel)

}