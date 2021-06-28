package com.soyle.stories.project.openProject

import com.soyle.stories.common.Notifier
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import kotlin.coroutines.coroutineContext

class ProjectOpenedNotifier : ProjectOpenedReceiver, Notifier<ProjectOpenedReceiver>() {
    override suspend fun receiveOpenedProject(openedProject: OpenProject.ResponseModel) {
        notifyAll { it.receiveOpenedProject(openedProject) }
    }
}