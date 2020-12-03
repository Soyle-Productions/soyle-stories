package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneDetails.SceneDetailsDriver
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.sceneDetails.SceneDetails
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneList.SceneList
import javafx.scene.input.KeyCode

fun SceneList.givenSceneDetailsToolHasBeenOpened(scene: Scene): SceneDetails =
    getSceneDetailsTool(scene) ?: openSceneDetailsTool(scene)

fun getSceneDetailsToolOrError(scene: Scene): SceneDetails =
    getSceneDetailsTool(scene) ?: error("Scene details is not open for ${scene.name}")
fun getSceneDetailsTool(scene: Scene): SceneDetails?
{
    val workbench = soyleStories.getAnyOpenWorkbenchOrError()
    return workbench.scope.toolScopes.asSequence()
        .filterIsInstance<SceneDetailsScope>()
        .find { it.sceneId == scene.id.uuid }
        ?.get<SceneDetails>()?.takeIf { it.root.parent != null }
}

fun SceneList.openSceneDetailsTool(scene: Scene): SceneDetails
{
    val driver = SceneListDriver(this)
    val item = driver.getSceneItemOrError(scene.name.value)
    driver.interact {
        driver.getTree().selectionModel.select(item)
        with(driver) {
            item.getSceneDetailsItem().fire()
        }
    }
    return getSceneDetailsToolOrError(scene)
}

fun SceneDetails.includeCharacter(character: Character)
{
    val driver = SceneDetailsDriver(this)
    val includeCharacterMenu = driver.getIncludeCharacterMenu()
    driver.interact {
        includeCharacterMenu.show()
        includeCharacterMenu.items.find { it.text == character.name.value }!!.fire()
    }
}

fun SceneDetails.setCharacterMotivation(character: Character, motivation: String)
{
    val driver = SceneDetailsDriver(this)
    val includedCharacter = driver.getIncludedCharacter(character.id.uuid.toString())
    val motivationInput = includedCharacter.getMotivationFieldInput()
    driver.interact {
        motivationInput.requestFocus()
        motivationInput.text = motivation
        driver.clickOn(driver.getIncludeCharacterMenu())
    }
}