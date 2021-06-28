package com.soyle.stories.project.openProject

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.workspace.usecases.openProject.OpenProject

class OpenProjectControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val openProject: OpenProject,
    private val openProjectOutput: OpenProject.OutputPort
) : OpenProjectController {

    override fun openProject(location: String) {
        threadTransformer.async {
            openProject.invoke(location, openProjectOutput)
        }
    }

    override fun forceOpenProject(location: String) {
        threadTransformer.async {
            openProject.forceOpenProject(location, openProjectOutput)
        }
    }

    override fun replaceOpenProject(location: String) {
        threadTransformer.async {
            openProject.replaceOpenProject(location, openProjectOutput)
        }
    }

}