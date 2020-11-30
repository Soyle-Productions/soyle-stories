package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.project.givenSettingsDialogHasBeenOpened
import com.soyle.stories.desktop.config.drivers.project.markConfirmDeleteSceneDialogUnNecessary
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert.Companion.assertThat
import com.soyle.stories.desktop.view.project.workbench.WorkbenchAssertions.Companion.assertThat
import com.soyle.stories.entities.Scene
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class SceneSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a scene named {string} has been created") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).getSceneByName(sceneName) ?: workbench.openCreateSceneDialog()
                .createSceneWithName(sceneName)
        }
        Given("the user has requested that a delete scene confirmation message not be shown") {
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSettingsDialogHasBeenOpened()
                .markConfirmDeleteSceneDialogUnNecessary()
        }
    }

    private fun whens() {
        When("a scene is created with the name {string}") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateSceneDialog()
                .createSceneWithName(sceneName)
        }
        When("{scene} is renamed with the name {string}") { scene: Scene, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .renameSceneTo(scene, newName)
        }
        When("the user wants to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .deleteScene(scene)
        }
    }

    private fun thens() {
        Then("a scene named {string} should have been created") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).getSceneByNameOrError(sceneName)

            val sceneList = workbench.givenSceneListToolHasBeenOpened()
            assertThat(sceneList) {
                hasSceneNamed(sceneName)
            }
        }
        Then(
            "the scene originally named {string} should have been renamed to {string}"
        ) { originalName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val (scene) = SceneDriver(workbench).getScenesAtOnePointNamed(originalName)

            assertEquals(newName, scene.name.value)

            val sceneList = workbench.givenSceneListToolHasBeenOpened()
            assertThat(sceneList) {
                doesNotHaveSceneNamed(originalName)
                hasSceneNamed(newName)
            }
        }
        Then("a delete scene confirmation message should be shown") {
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertThat(workbench) {
                hasConfirmDeleteSceneDialogOpen()
            }
        }
        Then("the {string} scene should not have been deleted") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).getSceneByNameOrError(sceneName)
        }
        Then("the {string} scene should have been deleted") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertNull(SceneDriver(workbench).getSceneByName(sceneName))
        }
    }

}