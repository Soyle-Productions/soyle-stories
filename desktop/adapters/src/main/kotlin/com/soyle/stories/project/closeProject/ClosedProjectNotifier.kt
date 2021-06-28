package com.soyle.stories.project.closeProject

import com.soyle.stories.common.Notifier
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import kotlin.coroutines.coroutineContext

class ClosedProjectNotifier : ClosedProjectReceiver, Notifier<ClosedProjectReceiver>() {
    override suspend fun receiveClosedProject(closedProject: CloseProject.ResponseModel) {
        notifyAll { it.receiveClosedProject(closedProject) }
    }
}