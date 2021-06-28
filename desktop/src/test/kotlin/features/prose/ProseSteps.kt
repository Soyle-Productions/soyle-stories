package com.soyle.stories.desktop.config.features.prose


import com.soyle.stories.desktop.config.drivers.prose.ProseDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.scene.Scene
import io.cucumber.java8.En

class ProseSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {

    }

    private fun whens() {
    }

    private fun thens() {
        Then("prose for {scene} should have been created") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ProseDriver(workbench).getProseByIdOrError(scene.proseId)
        }
    }

}