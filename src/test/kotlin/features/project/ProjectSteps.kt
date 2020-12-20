package com.soyle.stories.desktop.config.features.project

import com.soyle.stories.desktop.config.drivers.project.ProjectDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchForProjectOrError
import com.soyle.stories.desktop.config.features.soyleStories
import io.cucumber.java8.En

class ProjectSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a project has been started") {
            val project = ProjectDriver(soyleStories).givenProjectHasBeenStarted()
            soyleStories.getWorkbenchForProjectOrError(project.id.uuid)
        }
    }

    private fun whens() {

    }

    private fun thens() {

    }

}