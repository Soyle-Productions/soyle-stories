package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.scene.SceneDriver
import com.soyle.stories.desktop.config.drivers.scene.createSceneWithName
import com.soyle.stories.desktop.config.drivers.scene.givenSceneListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.scene.openCreateSceneDialog
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.createThemeWithName
import com.soyle.stories.desktop.config.drivers.theme.givenThemeListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.theme.openCreateThemeDialog
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert
import com.soyle.stories.desktop.view.theme.themeList.ThemeListAssert
import io.cucumber.java8.En

class SceneSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {

    }

    private fun whens() {
        When("a scene is created with the name {string}") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateSceneDialog()
                .createSceneWithName(sceneName)
        }
    }

    private fun thens() {
        Then("a scene named {string} should have been created") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).getSceneByNameOrError(sceneName)

            val sceneList = workbench.givenSceneListToolHasBeenOpened()
            SceneListAssert.assertThat(sceneList) {
                hasSceneNamed(sceneName)
            }
        }
    }

}