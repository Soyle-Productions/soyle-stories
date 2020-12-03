package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.project.givenSettingsDialogHasBeenOpened
import com.soyle.stories.desktop.config.drivers.project.markConfirmDeleteSceneDialogUnNecessary
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert.Companion.assertThat
import com.soyle.stories.desktop.view.project.workbench.WorkbenchAssertions.Companion.assertThat
import com.soyle.stories.desktop.view.scene.sceneDetails.SceneDetailsAssertions
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.repositories.SceneRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*

class SceneSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a scene named {string} has been created") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneCreatedWithName(sceneName)
        }
        Given("the user has requested that a delete scene confirmation message not be shown") {
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSettingsDialogHasBeenOpened()
                .markConfirmDeleteSceneDialogUnNecessary()
        }
        Given("the user wanted to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenDeleteSceneDialogHasBeenOpened(scene)
        }
        Given("the following scenes with motivations for characters") { dataTable: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val dataLists = dataTable.asLists()
            val scenes = dataLists[0].drop(1).map { workbench.givenSceneCreatedWithName(it) }
            dataLists.drop(1).forEach {
                val character = CharacterDriver(workbench).givenCharacterNamed(NonBlankString.create(it.first())!!)
                it.drop(1).forEachIndexed { index, s ->
                    if (s == "-") return@forEach
                    val scene = scenes[index]
                    val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                        .givenSceneDetailsToolHasBeenOpened(scene)
                    sceneDetailsTool.includeCharacter(character)
                    if (s != "inherit") {
                        sceneDetailsTool.setCharacterMotivation(character, s)
                    }
                }
            }

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
        When("the user confirms they want to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenDeleteSceneDialogHasBeenOpened(scene)
                .confirmDelete()
        }
        When("{scene} is deleted") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .openDeleteSceneDialog(scene)
            getDeleteSceneDialog()
                ?.confirmDelete()
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
        Then(
            "{scene} should not have a motivation for {character} anymore"
        ) { scene: Scene, character: Character ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasMotivationValue("")
                }
            }
        }
        Then(
            "{scene} should have {string} as {character}'s inherited motivation"
        ) { scene: Scene, expectedMotivation: String, character: Character ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)

            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasInheritedMotivationValue(expectedMotivation)
                }
            }
        }
    }

    companion object {
        private fun WorkBench.givenSceneCreatedWithName(sceneName: String): Scene {
            val driver = SceneDriver(this)
            return driver.getSceneByName(sceneName) ?: this.openCreateSceneDialog()
                .createSceneWithName(sceneName)
                .let { driver.getSceneByNameOrError(sceneName) }
        }
    }

}