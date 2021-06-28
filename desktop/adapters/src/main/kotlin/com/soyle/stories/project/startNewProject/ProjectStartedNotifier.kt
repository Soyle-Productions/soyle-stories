package com.soyle.stories.project.startNewProject

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.project.startNewProject.StartNewProject

class ProjectStartedNotifier : ProjectStartedReceiver, Notifier<ProjectStartedReceiver>() {

    override suspend fun receiveProjectStarted(projectStarted: StartNewProject.ResponseModel) {
        notifyAll { it.receiveProjectStarted(projectStarted) }
    }

}