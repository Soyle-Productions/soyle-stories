package com.soyle.stories.project.closeProject

import com.soyle.stories.domain.project.Project
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface CloseProjectController {

    fun closeProject(projectId: Project.Id): Job

    class Implementation(
        mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val closeProjectPrompt: CloseProjectPrompt,

        private val closeProject: CloseProject,
        private val closeProjectOutput: CloseProject.OutputPort
    ) : CloseProjectController, CoroutineScope by CoroutineScope(mainContext) {

        override fun closeProject(projectId: Project.Id): Job {
            return launch {
                val confirmed = closeProjectPrompt.requestConfirmation()
                if (!confirmed) return@launch

                withContext(asyncContext) {
                    closeProject.invoke(projectId.uuid, closeProjectOutput)
                }
            }
        }

    }

}