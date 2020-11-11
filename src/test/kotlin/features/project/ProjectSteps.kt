package com.soyle.stories.desktop.config.features.project

import com.soyle.stories.desktop.config.drivers.project.ProjectDriver
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.di.get
import com.soyle.stories.project.startNewProject.StartProjectController
import io.cucumber.java8.En

class ProjectSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a project has been started") {
            ProjectDriver(soyleStories).givenProjectHasBeenStarted()
        }
    }

    private fun whens() {

    }

    private fun thens() {

    }

}