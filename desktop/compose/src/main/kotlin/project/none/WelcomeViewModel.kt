package com.soyle.stories.desktop.view.project.none

import com.soyle.stories.project.startNewProject.StartProjectController
import org.koin.core.context.GlobalContext

class WelcomeViewModel(
    private val createNewProject: StartProjectController = GlobalContext.get().get()
) {

    fun startProject() {
        createNewProject.startProject()
    }

}